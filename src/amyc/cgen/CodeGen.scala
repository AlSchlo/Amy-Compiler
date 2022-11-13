package amyc
package cgen

import analyzer._
import ast.Identifier
import ast.SymbolicTreeModule.{IntType => AmyIntType, StringType => AmyStringType, Plus => AmyPlus, Minus => AmyMinus, Mod => AmyMod,
                                Div => AmyDiv, And => AmyAnd, Or => AmyOr, IntLiteral => AmyIntLiteral, StringLiteral => AmyStringLiteral, _}
import utils.{Context, Pipeline}
import c._
import Tokens.{Identifier => CIdentifier, _}
import Utils._
import scala.runtime.stdLibPatches.language.experimental.namedTypeArguments

// Generates C code from an Amy program
object CodeGen extends Pipeline[(Program, SymbolTable), CFile] {
  def run(ctx: Context)(v: (Program, SymbolTable)): CFile = {
    val (program, table) = v 
    
    // Generates code for a module
    def cgModule(moduleDef: ModuleDef): (List[Function], List[Struct]) = {
      val ModuleDef(name, defs, optExpr) = moduleDef
      // Generates code for all functions
      (defs.collect { case fd: FunDef if !builtInFunctions(fullName(name, fd.name)) =>
        cgFunction(fd, name, false)
      } ++
      // Generates code for the "main" function
      optExpr.toList.map { expr =>
        val mainFd = FunDef(Identifier.fresh("main"), Nil, TypeTree(AmyIntType), expr)
        cgFunction(mainFd, name, true)
      }, defs.collect{ case struct: CaseClassDef => cgStruct(struct, name) })
    }
    
    // Generates C struct declarations of module 'owner'
    def cgStruct(struct: CaseClassDef, owner: Identifier): Struct = {
      val name = fullName(owner, struct.name)
      val constr = table.getConstructor(owner.name, struct.name.name) match {
        case Some((_, c)) => c
        case None => ctx.reporter.fatal("Unfound constructor name. Error in name analysis!")
      }
      // Rename attributes to 'att' for simplicity, as we don't have access to the names (without having to refactor the existing codebase)
      new Struct(name, constr.index, constr.argTypes.zip(constr.argTypes.indices).map(e => (e._1, "att" + e._2)))
    }

    // Generates code for a function of module 'owner'
    def cgFunction(fd: FunDef, owner: Identifier, isMain: Boolean): Function = {
      val name = fullName(owner, fd.name) 
      new Function(name, fd.params, fd.retType.tpe, isMain, 
        cgReturnExpr(fd.body)(if isMain then Map() else fd.paramNames.zip(fd.params.map(p => p.tt.tpe)).map(v => (v._1.name, (v._1.name, v._2))).toMap))
    }

    // variableEnv is a dictionary from variable names in the Amy program to the corresponding variable names in the C program and its type.
    type VariableEnv = Map[String, (String, Type)]
  
    // Triplet representing a pure expression
    // (code of the pure expression, type of the pure expression, prerequisite of the pure Expression)
    type PureExpr = (Code, Type, Code)

    // Generates code for an expression expr. Upper level.
    def cgReturnExpr(expr: Expr)(variables: VariableEnv): Code = {
      val (code, _, prec) = cgExpr(expr)(variables)
      prec <> Return <-> code <> SemiColumn
    }

    // Generates code for an expression expr. Lower level (recursive).
    def cgExpr(expr: Expr)(implicit variables: VariableEnv): PureExpr = {
      expr match {
        case Variable(name) => (CIdentifier(variables(name.name)._1), variables(name.name)._2, Empty)
        case AmyIntLiteral(i) => (IntLiteral(i), AmyIntType, Empty)
        case BooleanLiteral(b) => (IntLiteral(if b then 1 else 0), BooleanType, Empty)
        case UnitLiteral() => (IntLiteral(0), UnitType, Empty)
        case AmyStringLiteral(s) => {
          val name = getFreshLabel("string")
          // Need to allocate dynamically in the case a biiger scope than the current block
          val alloc = StringType <-> CIdentifier(name) <-> Assign <-> Malloc <> OpenedParenthesis <> Strlen <> OpenedParenthesis 
                      <> StringLiteral(s) <> ClosedParenthesis <-> Plus <-> IntLiteral(1) <> ClosedParenthesis <> SemiColumn
          val assign = Strcpy <> OpenedParenthesis <> CIdentifier(name) <> Comma <-> StringLiteral(s) <> ClosedParenthesis <> SemiColumn
          (CIdentifier(name), AmyStringType, alloc <> assign)
        }
        case AmyPlus(lhs, rhs) => binOpIntCode(lhs, Plus, rhs)
        case AmyMinus(lhs, rhs) => binOpIntCode(lhs, Minus, rhs)
        case Times(lhs, rhs) => binOpIntCode(lhs, Mul, rhs)
        case AmyDiv(lhs, rhs) => binOpIntCode(lhs, Div, rhs)
        case AmyMod(lhs, rhs) => binOpIntCode(lhs, Mod, rhs)
        case LessThan(lhs, rhs) => binOpBoolCode(lhs, Lt, rhs)
        case LessEquals(lhs, rhs) => binOpBoolCode(lhs, Le, rhs)
        case AmyAnd(lhs, rhs) => binOpBoolCode(lhs, And, rhs)
        case AmyOr(lhs, rhs) => binOpBoolCode(lhs, Or, rhs)
        case Equals(lhs, rhs) => binOpBoolCode(lhs, Eq, rhs)
        case Concat(lhs, rhs) => {
          val name = getFreshLabel("string")
          val left = CIdentifier(getFreshLabel("left"))
          val right = CIdentifier (getFreshLabel("right"))
          val (lcode, _, lprec) = cgExpr(lhs)
          val (rcode, _, rprec) = cgExpr(rhs)
          // Need to allocate dynamically in the case scope of the String is bigger than the current block
          val alloc = StringType <-> left <-> Assign <-> lcode <> SemiColumn
                      <> StringType <-> right <-> Assign <-> rcode <> SemiColumn
                      <> StringType <-> CIdentifier(name) <-> Assign <-> Malloc <> OpenedParenthesis
                      <> Strlen <> OpenedParenthesis <> left <> ClosedParenthesis  <-> Plus <-> Strlen <> OpenedParenthesis 
                      <> right <> ClosedParenthesis <-> Plus <-> IntLiteral(1) <>  ClosedParenthesis <> SemiColumn;
          val assign = Strcpy <> OpenedParenthesis <> CIdentifier(name) <> Comma <-> left <> ClosedParenthesis <> SemiColumn
          val concat = Strcat <> OpenedParenthesis <> CIdentifier(name) <> Comma <-> right <> ClosedParenthesis <> SemiColumn
          (CIdentifier(name), AmyStringType, lprec <> rprec <> alloc <> assign <> concat)
        }
        case Not(e) => uniOpBoolCode(Exclamation, e)
        case Neg(e) => uniOpIntCode(Minus, e)
        case Call(qname, args) => {
          table.getFunction(qname) match {
            // It is a function
            case Some(sign) => {  
              val (arguments, _, prec) = if args.isEmpty then (Code(Nil), UnitType, Code(Nil)) // To make the compiler happy
                else args.map(cgExpr(_)).reduce((a1, a2) => (a1._1 <> Comma <-> a2._1, sign.retType, a1._3 <> a2._3))
              (CIdentifier(fullName(sign.owner, qname)) <> OpenedParenthesis <> arguments <> ClosedParenthesis, sign.retType, prec)     
            }
            // It is a constructor
            case None => table.getConstructor(qname) match {
              case Some(sign) => {
                val name = getFreshLabel("struct")
                val alloc = StructTk <-> CIdentifier(fullName(sign.owner, qname)) <> Ptr <-> CIdentifier(name) <-> Assign <-> Malloc
                                  <> OpenedParenthesis <> SizeOf <> OpenedParenthesis <> StructTk <-> CIdentifier(fullName(sign.owner, qname))
                                  <> ClosedParenthesis <> ClosedParenthesis <> SemiColumn 
                val constrIndex = CIdentifier(name) <> Arrow <> CIdentifier("constr_index") <-> Assign <-> IntLiteral(sign.index) <> SemiColumn
                if args.isEmpty then (CIdentifier(name), sign.retType, alloc <> constrIndex) else {
                  val cgArgs = args.map(cgExpr(_))
                  val precArg = cgArgs.map(_._3).reduce((a, b) => a <> b)
                  val assign = cgArgs.map(a => a._1).zipWithIndex
                                     .map((c,i) => CIdentifier(name) <> Arrow <> CIdentifier(s"att$i") <-> Assign <-> c)
                                     .reduce((a,b) => a <> SemiColumn <> b)
                  (CIdentifier(name), sign.retType, precArg <> alloc <> constrIndex <> assign <> SemiColumn)
                }
              }
              case None => ctx.reporter.fatal("Unfound call name. Error in name analysis!")
            }
          }
        }
        case Sequence(e1, e2) => {
          val (code1, _, prec1) = cgExpr(e1)
          val (code2, typ, prec2) = cgExpr(e2)
          (code2, typ, prec1 <> code1 <> SemiColumn <> prec2)
        }
        case Let(df, value, body) => {
          val tpe = typeToToken(df.tt.tpe)
          val name = getFreshLabel(df.name.name)
          val (codeV, _, precV) = cgExpr(value)
          val (codeB, typ, precB) = cgExpr(body)(variables + (df.name.name -> (name, df.tt.tpe)))
          (codeB, typ, precV <> tpe <-> CIdentifier(name) <-> Assign <-> codeV <> SemiColumn <> precB)
        }
        case Ite(cond, thenn, elze) => {
          val (cCond, _, pCond) = cgExpr(cond)
          val (cThenn, _, pThenn) = cgExpr(thenn)
          val (cElze, typ, pElze) = cgExpr(elze)
          val name = getFreshLabel("ite")
          val prec = pCond
                    <> typeToToken(typ) <-> CIdentifier(name) <> SemiColumn 
                    <> If <> OpenedParenthesis <> cCond <> ClosedParenthesis
                    <-> OpenedBrace <> pThenn <> CIdentifier(name) <-> Assign <-> cThenn <> SemiColumn <> ClosedBrace
                    <> Else <-> OpenedBrace <> pElze <> CIdentifier(name) <-> Assign <-> cElze <> SemiColumn <> ClosedBrace
          (CIdentifier(name), typ, prec)
        }
        case Match(scrut, cases) => {
          // Generates matching conditions
          // (Code, Code, Code, VariableEnv) = (booleanPureValue, Prerequisite, Postrequisite, VariableEnv) -> See in report
          def matchCond(pat: Pattern, codeScrut: Code, typeScrut: Type): (Code, Code, Code, VariableEnv) = pat match {
            case WildcardPattern() => (IntLiteral(1), Empty, Empty, Map())
            case IdPattern(id) => {
              val name = getFreshLabel(id.name)
              (IntLiteral(1), Empty, typeToToken(typeScrut) <-> CIdentifier(name) <-> Assign <-> codeScrut <> SemiColumn, Map((id.name -> (name, typeScrut))))
            }
            case LiteralPattern(lit) => {
              val (code, _, prec) = cgExpr(lit)
              (codeScrut <-> Eq <-> code, prec, Empty, Map())
            }
            case CaseClassPattern(constr, args) => {
              table.getConstructor(constr) match {
                  case Some(sign) => {
                    val testConst = OpenedParenthesis <> Ptr <> OpenedParenthesis <> IntType <> Ptr <> ClosedParenthesis <> codeScrut 
                                    <> ClosedParenthesis <-> Eq <-> IntLiteral(sign.index) 
                    if args.isEmpty then (testConst, Code(Nil), Code(Nil), Map()) else {
                      val testParam = args.zipWithIndex.map((pattern, i) => matchCond(pattern,
                        OpenedParenthesis <> OpenedParenthesis <> StructTk <-> CIdentifier(fullName(sign.owner, constr)) <> Ptr <> ClosedParenthesis <> codeScrut <> ClosedParenthesis <> Arrow <> CIdentifier(s"att$i") ,
                        sign.argTypes(i)))
                      val code = testConst <-> And <-> testParam.map(_._1).reduce((c1, c2) => c1 <-> And <-> c2)
                      val prec = testParam.map(_._2).reduce((c1, c2) => c1 <> c2)
                      val env = testParam.map(_._3).reduce((c1, c2) => c1 <> c2)
                      val map = testParam.map(_._4).reduce((m1, m2) => m1 ++ m2)
                      (code, prec, env, map)
                    }
                  }
                  case None => ctx.reporter.fatal("Unfound match name. Error in name analysis!")
              }
            }
          }
          
          val name = getFreshLabel("match")
          val (codeScrut, typeScrut, precScrut) = cgExpr(scrut)
          val casesList = cases.map(mc => {
            val (testCaseCode, precTest,envInit, env) = matchCond(mc.pat, codeScrut, typeScrut)
            val (nextCode, typ, precNextCode) = cgExpr(mc.expr)(variables ++ env)
            val prec = precTest
                    <> If <> OpenedParenthesis <> testCaseCode <> ClosedParenthesis
                    <-> OpenedBrace <> envInit <> precNextCode  <> CIdentifier(name) <-> Assign <-> nextCode <> SemiColumn <> ClosedBrace
                    <> Else <-> OpenedBrace            
            (Code(List(CIdentifier(name))), typ, prec)
          })
          val code = Code(List(CIdentifier(name)))
          val typ = casesList(0)._2 // We rely on Amyc type checking
          val prec = casesList.map(_._3).reduce(_ <> _) <> CError <> OpenedParenthesis <> StringLiteral("Match error!") <> ClosedParenthesis <> SemiColumn <> cases.map(c => ClosedBrace)
          (code, typ, precScrut <> typeToToken(typ) <-> CIdentifier(name) <> SemiColumn <> prec)   
      }
        case Error(e) => {
          val(code, typ, prec) = cgExpr(e)
          (CError <> OpenedParenthesis <> code <> ClosedParenthesis, typ, prec)
        }
      }
    }
    
    //.................................//
    //........Helper Functions ........//
    //.................................//
    def binOpIntCode(lhs: Expr, op: Token, rhs: Expr)(implicit variables: VariableEnv): PureExpr = {
      val (code1, _, prec1) = cgExpr(lhs)
      val (code2, _, prec2) = cgExpr(rhs)
      (OpenedParenthesis <> code1 <-> op <-> code2 <> ClosedParenthesis, AmyIntType, prec1 <> prec2)
    }

    def binOpBoolCode(lhs: Expr, op: Token, rhs: Expr)(implicit variables: VariableEnv): PureExpr = {
      val (code1, _, prec1) = cgExpr(lhs)
      val (code2, _, prec2) = cgExpr(rhs)
      (OpenedParenthesis <> code1 <-> op <-> code2 <> ClosedParenthesis, BooleanType, prec1 <> prec2)
    }

    def uniOpIntCode(op: Token, e: Expr)(implicit variables: VariableEnv): PureExpr = {
      val (code, _, prec) = cgExpr(e)
      (OpenedParenthesis <> op <> code <> ClosedParenthesis, AmyIntType, prec)
    }

    def uniOpBoolCode(op: Token, e: Expr)(implicit variables: VariableEnv): PureExpr = {
      val (code, _, prec) = cgExpr(e)
      (OpenedParenthesis <> op <> code <> ClosedParenthesis, BooleanType, prec)
    }
    
    // Creates the CFile
    CFile(program.modules.last.name.name, defaultImports, program.modules.flatMap(m => cgModule(m)._1), program.modules.flatMap(m => cgModule(m)._2))
  }
}