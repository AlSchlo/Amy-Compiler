package amyc
package parsing

import scala.language.implicitConversions

import amyc.ast.NominalTreeModule._
import amyc.utils._
import Tokens._
import TokenKinds._

import scallion._

// The parser for Amy
object Parser extends Pipeline[Iterator[Token], Program] with Parsers {

  type Token = amyc.parsing.Token
  type Kind = amyc.parsing.TokenKind

  import Implicits._

  override def getKind(token: Token): TokenKind = TokenKind.of(token)

  val eof: Syntax[Token] = elem(EOFKind)
  def op(string: String): Syntax[Token] = elem(OperatorKind(string))
  def kw(string: String): Syntax[Token] = elem(KeywordKind(string))

  implicit def delimiter(string: String): Syntax[Token] = elem(DelimiterKind(string))

  // An entire program (the starting rule for any Amy file).
  lazy val program: Syntax[Program] = many1(many1(module) ~<~ eof).map(ms => Program(ms.flatten.toList).setPos(ms.head.head))

  // A module (i.e., a collection of definitions and an initializer expression)
  lazy val module: Syntax[ModuleDef] = (kw("object") ~ identifier ~ many(definition) ~ opt(expr) ~ kw("end") ~ identifier).map {
    case obj ~ id ~ defs ~ body ~ _ ~ id1 => 
      if id == id1 then 
        ModuleDef(id, defs.toList, body).setPos(obj)
      else 
        throw new AmycFatalError("Begin and end module names do not match: " + id + " and " + id1)
  }

  // An identifier.
  val identifier: Syntax[String] = accept(IdentifierKind) {  
    case IdentifierToken(name) => name
  }

  // An identifier along with its position.
  val identifierPos: Syntax[(String, Position)] = accept(IdentifierKind) {
    case id@IdentifierToken(name) => (name, id.position)
  }

  // A definition within a module.
  lazy val definition: Syntax[ClassOrFunDef] = abstractClassDef | caseClassDef | funDef

  lazy val abstractClassDef: Syntax[ClassOrFunDef] = (kw("abstract") ~ kw("class") ~ identifier).map {
    case abs ~ _ ~ id => AbstractClassDef(id).setPos(abs)
  }

  lazy val caseClassDef: Syntax[ClassOrFunDef] = (kw("case") ~ kw("class") ~ identifier ~ "(" ~ parameters ~ ")" ~ kw("extends") ~ identifier).map {
    case cas ~ _ ~ id ~ _ ~ params ~ _ ~ _ ~ parent => CaseClassDef(id, params.map(_.tt), parent).setPos(cas)
  }

  lazy val funDef: Syntax[ClassOrFunDef] = (kw("fn") ~ identifier ~ "(" ~ parameters ~ ")" ~ ":" ~ typeTree ~ "=" ~ "{" ~ expr ~ "}").map {
    case fn ~ id ~ _ ~ params ~ _ ~ _ ~ typ ~ _ ~ _ ~ body ~ _ => FunDef(id, params, typ, body).setPos(fn) 
  }

  // A list of parameter definitions.
  lazy val parameters: Syntax[List[ParamDef]] = repsep(parameter, ",").map(_.toList)

  // A parameter definition, i.e., an identifier along with the expected type.
  lazy val parameter: Syntax[ParamDef] = (identifierPos ~ ":" ~ typeTree).map {
    case (name,pos) ~ _ ~ typ => ParamDef(name, typ).setPos(pos)
  }

  // A type expression.
  lazy val typeTree: Syntax[TypeTree] = primitiveType | identifierType

  // A built-in type (such as `Int`).
  val primitiveType: Syntax[TypeTree] = (accept(PrimTypeKind) {
    case tk@PrimTypeToken(name) => TypeTree(name match {
      case "Unit" => UnitType
      case "Boolean" => BooleanType
      case "Int" => IntType
      case "String" => StringType
      case _ => throw new java.lang.Error("Unexpected primitive type name: " + name)
    }).setPos(tk)
  } ~ opt("(" ~ literal ~ ")")).map { 
    case (prim@TypeTree(IntType)) ~ Some(_ ~ IntLiteral(32) ~ _) => prim
    case TypeTree(IntType) ~ Some(_ ~ IntLiteral(width) ~ _) => 
      throw new AmycFatalError("Int type can only be used with a width of 32 bits, found : " + width)
    case TypeTree(IntType) ~ Some(_ ~ lit ~ _) =>
      throw new AmycFatalError("Int type should have an integer width (only 32 bits is supported)")
    case TypeTree(IntType) ~ None => 
      throw new AmycFatalError("Int type should have a specific width (only 32 bits is supported)")
    case prim ~ Some(_) => 
      throw new AmycFatalError("Only Int type can have a specific width")
    case prim ~ None => prim
  }

  // A user-defined type (such as `List`).
  lazy val identifierType: Syntax[TypeTree] = (identifierPos ~ opt("." ~ identifier)).map {
    case (name,pos) ~ None => TypeTree(ClassType(QualifiedName(None, name))).setPos(pos)
    case (module,pos) ~ Some(_ ~ name) => TypeTree(ClassType(QualifiedName(Some(module), name))).setPos(pos)
  }

  // An expression.
  lazy val expr: Syntax[Expr] = recursive { 
    valExpr | (exprlvl2 ~ opt(";" ~ expr)).map {
      case e ~ None => e
      case e1 ~ Some(_ ~ e2) => Sequence(e1, e2).setPos(e1)
    }     
  }

  lazy val valExpr: Syntax[Expr] = (kw("val") ~ parameter ~ "=" ~ exprlvl2 ~ ";" ~ expr).map {
    case va ~ df ~ _ ~ value ~ _ ~ body => Let(df, value, body).setPos(va)
  }

  lazy val exprlvl2: Syntax[Expr] = recursive {
    (ifExpr ~ opt(matchings)).map {
      case ife ~ None => ife
      case ife ~ Some(m) => m.foldLeft(ife)((e, m) => Match(e, m)).setPos(ife)
    } | (exprlvl3to8 ~ opt(matchings)).map{
      case e ~ None => e
      case e ~ Some(m) => m.foldLeft(e)((e, m) => Match(e, m)).setPos(e)
    }
  }
    
  lazy val matchings: Syntax[List[List[MatchCase]]] = recursive {
    many1(kw("match") ~ "{" ~ many1(matchCase) ~ "}").map {
      case m => m.map(e => e match  {
        case (_ ~ _ ~ m ~ _) => m.toList
      }).toList
    }
  }
  
  lazy val ifExpr: Syntax[Expr] = (kw("if") ~ "(" ~ expr ~ ")" ~ "{" ~ expr ~ "}" ~ kw("else") ~ "{" ~ expr ~ "}").map {
    case ifs ~ _ ~ cond ~ _ ~ _ ~ thenn ~ _ ~ _ ~ _ ~ elze ~ _ => Ite(cond, thenn, elze).setPos(ifs)
  }

  lazy val matchCase: Syntax[MatchCase] = (kw("case") ~ pattern ~ "=>" ~ expr).map {
    case cas ~ pat ~ _ ~ e => MatchCase(pat, e).setPos(cas)
  }

  // A pattern as part of a mach case.
  lazy val pattern: Syntax[Pattern] = recursive {  
    literalPattern | wildPattern | caseClassPattern
  }

  lazy val caseClassPattern: Syntax[Pattern] = (identifierPos ~ opt(opt("." ~ identifier) ~ "(" ~ repsep(pattern, ",") ~ ")")).map {
      case (id,pos) ~ None => IdPattern(id).setPos(pos)
      case (id,pos) ~ Some(None ~ _ ~ pat ~ _) => CaseClassPattern(QualifiedName(None, id), pat.toList).setPos(pos)
      case (module,pos) ~ Some(Some( _ ~ id) ~ _ ~ pat ~ _ ) => CaseClassPattern(QualifiedName(Some(module), id), pat.toList).setPos(pos)
  }

  lazy val literalPattern: Syntax[Pattern] = literal.map(lit => LiteralPattern(lit).setPos(lit)) | ("("~")").map{ case pos ~ _ => LiteralPattern(UnitLiteral()).setPos(pos)}

  lazy val wildPattern: Syntax[Pattern] = kw("_").map(wild => WildcardPattern().setPos(wild))

  lazy val exprlvl3to8: Syntax[Expr] = recursive {
    operators(exprlvl9)( 
      op("*") | op("/") | op("%") is LeftAssociative,
      op("+") | op("-") | op("++") is LeftAssociative,
      op("<") | op("<=") is LeftAssociative,
      op("&&") | op("==") is LeftAssociative,
      op("||") is LeftAssociative
    ) {
      case (l, OperatorToken("||"), r) => Or(l, r).setPos(l)
      case (l, OperatorToken("&&"), r) => And(l, r).setPos(l)
      case (l, OperatorToken("=="), r) => Equals(l, r).setPos(l)
      case (l, OperatorToken("<"), r) => LessThan(l, r).setPos(l)
      case (l, OperatorToken("<="), r) => LessEquals(l, r).setPos(l)
      case (l, OperatorToken("+"), r) => Plus(l, r).setPos(l)
      case (l, OperatorToken("-"), r) => Minus(l, r).setPos(l)
      case (l, OperatorToken("++"), r) => Concat(l, r).setPos(l)
      case (l, OperatorToken("*"), r) => Times(l, r).setPos(l)
      case (l, OperatorToken("/"), r) => Div(l, r).setPos(l)
      case (l,OperatorToken("%"), r) => Mod(l, r).setPos(l)
      case (l, _, r) => throw new AmycFatalError("Unvalid operation")
    }
  }

  lazy val exprlvl9: Syntax[Expr] = recursive {
      ((op("-") | op("!")) ~ exprlvl10).map {
        case (pos@OperatorToken("-")) ~ e => Neg(e).setPos(pos)
        case (pos@OperatorToken("!")) ~ e => Not(e).setPos(pos)
        case op ~ e => throw new AmycFatalError("Not a unary operator")
      } | exprlvl10
  }

  lazy val exprlvl10: Syntax[Expr] = literal.up[Expr] | variableOrCall | errorExpr | paranthesisOrUnitExpr

  lazy val errorExpr: Syntax[Expr] = (kw("error") ~ "(" ~ expr ~ ")").map{
    case err ~ _ ~ msg ~ _ => Error(msg).setPos(err)
  }

  lazy val paranthesisOrUnitExpr: Syntax[Expr] = ("(" ~ opt(expr) ~ ")").map{
    case par ~ None ~ _ => UnitLiteral().setPos(par)
    case par ~ Some(e) ~ _ => e.setPos(par)
  }

  lazy val variableOrCall: Syntax[Expr] = (identifierPos ~ opt(opt("." ~ identifier) ~ "(" ~ repsep(expr, ",") ~ ")")).map {
      case (id, pos) ~ None => Variable(id).setPos(pos)
      case (id, pos) ~ Some(None ~ _ ~ args ~ _) => Call(QualifiedName(None, id), args.toList).setPos(pos)
      case (module, pos) ~ Some(Some(_ ~ id) ~ _ ~ args ~ _) => Call(QualifiedName(Some(module), id), args.toList).setPos(pos)
  }

  // A literal expression
  lazy val literal: Syntax[Literal[_]] = accept(LiteralKind) {
    case tk@BoolLitToken(b) => BooleanLiteral(b).setPos(tk)
    case tk@StringLitToken(s) => StringLiteral(s).setPos(tk)
    case tk@IntLitToken(i) => IntLiteral(i).setPos(tk)
  }

  // Ensures the grammar is in LL(1)
  lazy val checkLL1: Boolean = {
    if (program.isLL1) {
      true
    } else {
      // Set `showTrails` to true to make Scallion generate some counterexamples for you.
      // Depending on your grammar, this may be very slow.
      val showTrails = false
      debug(program, showTrails)
      false
    }
  }

  override def run(ctx: Context)(tokens: Iterator[Token]): Program = {
    import ctx.reporter._
    if (!checkLL1) {
      ctx.reporter.fatal("Program grammar is not LL1!")
    }

    val parser = Parser(program)

    parser(tokens) match {
      case Parsed(result, rest) => result
      case UnexpectedEnd(rest) => fatal("Unexpected end of input.")
      case UnexpectedToken(token, rest) => fatal("Unexpected token: " + token + ", possible kinds: " + rest.first.map(_.toString).mkString(", "))
    }
  }
}
