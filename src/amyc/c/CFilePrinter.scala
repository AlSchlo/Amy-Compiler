package amyc.c

import scala.language.implicitConversions
import amyc.cgen.Utils._
import amyc.utils._
import Tokens._
import amyc.ast.SymbolicTreeModule.{IntType => AmyIntType, BooleanType => AmyBooleanType, StringType => AmyStringType, UnitType => AmyUnitType, ClassType => AmyClassType, Type}

// The printer for a CFile
object CFilePrinter {
  private implicit def s2d(s: String): Raw = Raw(s)

  // Makes the entire file
  private def mkFile(f: CFile): Document = 
    Stacked(
      List(
        Stacked(f.imports map mkImport), 
        Stacked(f.structs map mkStruct, true),
        Stacked(f.functions.filter(f => !f.isMain) map mkSign),
        Stacked(f.functions map mkFun, true)
      ).filter({case Stacked(l, b) => !l.isEmpty}), true
    )

  // Handles the imports
  private def mkImport(s: String): Document = Lined(List("#include ", s))

  // Handles the functions
  private def mkFun(fh: Function): Document = {
    val fullName = fh.fullName
    val isMain = fh.isMain
    
    val nameDoc: Document = if (isMain) "main" else fullName
    val paramsDoc: Document = if (isMain) "()" else
      Lined(List("(", Lined(List(Lined(fh.params.map(p => (typeToDoc(p.tt.tpe) <:> " " <:> p.name.name)), ", "))), ")"))
    
    val resultDoc: Document = if (isMain) "int" else typeToDoc(fh.retType)
    Stacked(
      Lined(List(resultDoc, Lined(List(nameDoc, paramsDoc)), "{"), " "),
      Indented(Stacked(mkLines(mkCode(fh.code)))),
      "}"
    )
  }

  // Handles the functions prototypes (signatures)
  private def mkSign(fh: Function): Document = {
    val nameDoc = fh.fullName
    val paramsDoc: Document = Lined(List("(", Lined(List(Lined(fh.params.map(p => (typeToDoc(p.tt.tpe) <:> " " <:> p.name.name)), ", "))), ")"))
    val resultDoc: Document = typeToDoc(fh.retType)
    Lined(List(Lined(List(resultDoc, nameDoc), " "), paramsDoc, ";"))
  }

  // Handles the structs declaration
  private def mkStruct(strct: Struct): Document = {
    val nameDoc: Document = strct.fullName
    val paramsDoc: Document = Stacked(strct.params.map(e => typeToDoc(e._1) <:> " " <:> e._2 <:> ";").toList)
    if strct.params.isEmpty then Stacked(Lined(List(mkToken(StructTk), nameDoc, "{"), " "), Indented(Raw("int32_t constr_index;")), "};")
      else Stacked(Lined(List(mkToken(StructTk), nameDoc, "{"), " "), Indented(Raw("int32_t constr_index;")), Indented(paramsDoc), "};")
  }
  
  // Effectively generates a document from code
  private def mkCode(code: Code): List[Document] = code.tokens.map(tk => mkToken(tk))

  // Handles the token printing 
  private def mkToken(token: Token): Document = {
    token match {
      
      case IntLiteral(value) => value.toString
      case StringLiteral(value) => s"\"$value\""
      case Identifier(value) => value
      
      case IntType => "int32_t"
      case VoidType=> "void"
      case StringType => "char*" 
      
      case Plus => "+"
      case Minus => "-"
      case Mul => "*"
      case Div => "/"
      case Mod => "%"
      case And => "&&"
      case Or  => "||"
      case Lt => "<"
      case Le => "<="
      case Eq => "=="
      case Assign => "="

      case Exclamation => "!"
      case Column => ":"
      case SemiColumn => ";"
      case Comma => ","
      case QuestionMark => "?"
      case Return => "return"
      case OpenedParenthesis => "("
      case ClosedParenthesis => ")"
      case OpenedBrace => "{"
      case ClosedBrace => "}"

      case ReturnLine => "\n"
      case Space => " "
      case Empty=> ""

      case StructTk => "struct"
      case Ptr => "*"
      case Arrow => "->"

      case If => "if"
      case Else => "else"

      case Strlen => "strlen"
      case Malloc => "malloc"
      case Strcpy => "strcpy"
      case Strcat => "strcat"
      case SizeOf => "sizeof"
      case CError => "Error_error" // Call for a library function that prints a message and throws an assert error (see error error.h in cout)

      case Comment(msg) => s"/* $msg */"
    }
  }

  // Utility function to get the right type for function signatures
  private def typeToDoc(tpe: Type): Document = {
    tpe match {
      case AmyIntType => mkToken(IntType)
      case AmyBooleanType => mkToken(IntType)
      case AmyStringType => mkToken(StringType)
      case AmyUnitType => mkToken(IntType) 
      case AmyClassType(qName) => Raw("void*") 
    }
  }

  // Function responsible for the pretty printing of the CFile.
  // Takes the previous list of documents from the token mapping and transforms it into real lines.
  // Concretely, this means handling the indentation when opening and closing brackets as well as creating
  // a new line when we encounter a semicolumn.
  private def mkLines(ls: List[Document]): List[Document] = {
    def aux(acc: List[Document], rem: List[Document], line: String, tabsNb: Int): List[Document] = {
        rem match {
          case Nil => if(line.isEmpty) then acc.reverse else (Raw(line) :: acc).reverse
          case h :: t => h.print match {
            case ";" => aux(("  " * tabsNb + line + ";") :: acc, t, "", tabsNb)
            case "{" => aux(("  " * tabsNb + line + "{") :: acc, t, "", tabsNb + 1)
            case "}" => aux(("  " * (tabsNb - 1) + line + "}") :: acc, t, "", tabsNb - 1)
            case _ => aux(acc, t, line + h.print, tabsNb)
          }
        }
    }
    aux(Nil, ls, "", 0)
  }

  def apply(f: CFile) = mkFile(f).print
  def apply(fh: Function) = mkFun(fh).print
  def apply(strct: Struct) = mkStruct(strct).print
  def apply(token: Token) = mkToken(token).print
}