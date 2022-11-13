package amyc
package analyzer

import utils._
import ast.SymbolicTreeModule._
import ast.Identifier
import scala.collection.immutable.Stream.Cons

// The type checker for Amy
// Takes a symbolic program and rejects it if it does not follow the Amy typing rules.
object TypeChecker extends Pipeline[(Program, SymbolTable), (Program, SymbolTable)] {

  def run(ctx: Context)(v: (Program, SymbolTable)): (Program, SymbolTable) = {
    import ctx.reporter._

    val (program, table) = v

    case class Constraint(found: Type, expected: Type, pos: Position)

    // Represents a type variable.
    // It extends Type, but it is meant only for internal type checker use,
    //  since no Amy value can have such type.
    case class TypeVariable private (id: Int) extends Type
    object TypeVariable {
      private val c = new UniqueCounter[Unit]
      def fresh(): TypeVariable = TypeVariable(c.next(()))
    }

    // Generates typing constraints for an expression `e` with a given expected type.
    // The environment `env` contains all currently available bindings (you will have to
    //  extend these, e.g., to account for local variables).
    // Returns a list of constraints among types. These will later be solved via unification.
    def genConstraints(e: Expr, expected: Type)(implicit env: Map[Identifier, Type]): List[Constraint] = {
      
      // This helper returns a list of a single constraint recording the type
      //  that we found (or generated) for the current expression `e`
      def topLevelConstraint(found: Type): List[Constraint] =
        List(Constraint(found, expected, e.position))
      
      e match {

        case Variable(x) => topLevelConstraint(env(x))

        case IntLiteral(_) => topLevelConstraint(IntType)

        case UnitLiteral() => topLevelConstraint(UnitType)

        case BooleanLiteral(_) => topLevelConstraint(BooleanType)

        case StringLiteral(_) => topLevelConstraint(StringType)

        case Plus(lhs, rhs) => topLevelConstraint(IntType) ++ genConstraints(lhs, IntType) ++ genConstraints(rhs, IntType)

        case Minus(lhs, rhs) => topLevelConstraint(IntType) ++ genConstraints(lhs, IntType) ++ genConstraints(rhs, IntType)

        case Times(lhs, rhs) => topLevelConstraint(IntType) ++ genConstraints(lhs, IntType) ++ genConstraints(rhs, IntType)

        case Div(lhs, rhs) => topLevelConstraint(IntType) ++ genConstraints(lhs, IntType) ++ genConstraints(rhs, IntType)

        case Mod(lhs, rhs) => topLevelConstraint(IntType) ++ genConstraints(lhs, IntType) ++ genConstraints(rhs, IntType)

        case LessThan(lhs, rhs) => topLevelConstraint(BooleanType) ++ genConstraints(lhs, IntType) ++ genConstraints(rhs, IntType)

        case LessEquals(lhs, rhs) => topLevelConstraint(BooleanType) ++ genConstraints(lhs, IntType) ++ genConstraints(rhs, IntType)

        case And(lhs, rhs) => topLevelConstraint(BooleanType) ++ genConstraints(lhs, BooleanType) ++ genConstraints(rhs, BooleanType)

        case Or(lhs, rhs) => topLevelConstraint(BooleanType) ++ genConstraints(lhs, BooleanType) ++ genConstraints(rhs, BooleanType)

        case Equals(lhs, rhs) =>
          val a = TypeVariable.fresh()
          topLevelConstraint(BooleanType) ++ genConstraints(lhs, a) ++ genConstraints(rhs, a)  

        case Concat(lhs, rhs) => topLevelConstraint(StringType) ++ genConstraints(lhs, StringType) ++ genConstraints(rhs, StringType)

        case Not(e) => topLevelConstraint(BooleanType) ++ genConstraints(e, BooleanType)

        case Neg(e) => topLevelConstraint(IntType) ++ genConstraints(e, IntType)

        case Call(qname, args) =>
          table.getFunction(qname) match {
            case Some(sign) => topLevelConstraint(sign.retType) ++ args.zip(sign.argTypes).flatMap((e, t) => genConstraints(e, t))
            case None => table.getConstructor(qname) match {
                case Some(sign) => topLevelConstraint(sign.retType) ++ args.zip(sign.argTypes).flatMap((e, t) => genConstraints(e, t))
                case None => throw new AmycFatalError("Unfound call name. Error in name analysis!")
              }
          }
           
        case Sequence(e1, e2) =>
          val a = TypeVariable.fresh()
          genConstraints(e1, a) ++ genConstraints(e2, expected)
        
        case Let(df, value, body) =>
          val a = df.tt.tpe
          genConstraints(value, a) ++ genConstraints(body, expected)(env + (df.name -> a))

        case Ite(cond, thenn, elze) =>
          genConstraints(cond, BooleanType) ++ genConstraints(thenn, expected) ++ genConstraints(elze, expected) 
        
        case Match(scrut, cases) =>
          // Returns additional constraints from within the pattern with all bindings
          // from identifiers to types for names bound in the pattern.
          // (This is analogous to `transformPattern` in NameAnalyzer.)
          def handlePattern(pat: Pattern, scrutExpected: Type):
            (List[Constraint], Map[Identifier, Type]) =
          {
            pat match {
              case WildcardPattern() => (Nil, env)
              case IdPattern(name) => (Nil, env + (name -> scrutExpected))
              case LiteralPattern(lit) => (genConstraints(lit, scrutExpected), env)
              case CaseClassPattern(constr, args) =>
                val sign = table.getConstructor(constr).get
                val interArgs = args.zip(sign.argTypes).map((p, se) => handlePattern(p, se))
                val constrArgs = interArgs.flatMap(p => p._1)
                val envArgs = interArgs.flatMap(p => p._2).toMap
                (List(Constraint(sign.retType, scrutExpected, e.position)) ++ constrArgs, env ++ envArgs)
            }
          }

          def handleCase(cse: MatchCase, scrutExpected: Type): List[Constraint] = {
            val (patConstraints, newEnv) = handlePattern(cse.pat, scrutExpected)
            patConstraints ++ genConstraints(cse.expr, expected)(newEnv)
          }

          val st = TypeVariable.fresh()
          genConstraints(scrut, st) ++ cases.flatMap(cse => handleCase(cse, st))

        case Error(s) => genConstraints(s, StringType)
      }
    }


    // Given a list of constraints `constraints`, replace every occurence of type variable
    //  with id `from` by type `to`.
    def subst_*(constraints: List[Constraint], from: Int, to: Type): List[Constraint] = {
      // Do a single substitution.
      def subst(tpe: Type, from: Int, to: Type): Type = {
        tpe match {
          case TypeVariable(`from`) => to
          case other => other
        }
      }

      constraints map { case Constraint(found, expected, pos) =>
        Constraint(subst(found, from, to), subst(expected, from, to), pos)
      }
    }

    // Solve the given set of typing constraints and report errors
    //  using `ctx.reporter.error` if they are not satisfiable.
    // We consider a set of constraints to be satisfiable exactly if they unify.
    def solveConstraints(constraints: List[Constraint]): Unit = {
      constraints match { // Heavy code that could be refactored later. Case classes are already compared in depth in Scala.
        case Nil => ()
        case Constraint(found, expected, pos) :: more =>
          (found, expected) match {
            case (IntType, IntType) => solveConstraints(more)
            case (BooleanType, BooleanType) => solveConstraints(more)
            case (UnitType, UnitType) => solveConstraints(more)
            case (StringType, StringType) => solveConstraints(more)
            case (ClassType(q1), ClassType(q2)) => if(q1 == q2) then solveConstraints(more) else throwTypeError(found, expected, pos)
            case (TypeVariable(id1), t2@TypeVariable(id2)) => if(id1 == id2) then solveConstraints(more) else solveConstraints(subst_*(more, id1, t2))
            case (_, TypeVariable(_)) => solveConstraints(Constraint(expected, found, pos) :: more)
            case (TypeVariable(id), t) => solveConstraints(subst_*(more, id, t))
            case _ => throwTypeError(found, expected, pos)
          }
      }
    }

    def throwTypeError(found: Type, expected: Type, pos: Position): Unit =
      ctx.reporter.error("Type error: " + found + " found" + " but was expected " + expected + " at position " + pos)

    // Putting it all together to type-check each module's functions and main expression.
    program.modules.foreach { mod =>
      // Put function parameters to the symbol table, then typecheck them against the return type
      mod.defs.collect { case FunDef(_, params, retType, body) =>
        val env = params.map{ case ParamDef(name, tt) => name -> tt.tpe }.toMap
        solveConstraints(genConstraints(body, retType.tpe)(env))
      }

      // Type-check expression if present. We allow the result to be of an arbitrary type by
      // passing a fresh (and therefore unconstrained) type variable as the expected type.
      val tv = TypeVariable.fresh()
      mod.optExpr.foreach(e => solveConstraints(genConstraints(e, tv)(Map())))
    }

    v

  }
}
