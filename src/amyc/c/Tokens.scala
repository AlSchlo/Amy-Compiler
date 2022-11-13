package amyc
package c

import scala.language.implicitConversions

// An abstraction of C tokens
object Tokens {
  abstract class Token

  case class IntLiteral(value: Int) extends Token
  case class StringLiteral(value: String) extends Token
  case class Identifier(name: String) extends Token

  case object IntType extends Token
  case object VoidType extends Token
  case object StringType extends Token

  case object Plus extends Token
  case object Minus extends Token
  case object Mul extends Token
  case object Div extends Token
  case object Mod extends Token
  case object And extends Token
  case object Or extends Token

  case object Lt extends Token // Signed less-than
  case object Le extends Token // Signed less-equals
  case object Eq extends Token
  case object Assign extends Token

  case object Exclamation extends Token  
  case object Column extends Token
  case object SemiColumn extends Token
  case object Comma extends Token
  case object QuestionMark extends Token
  case object Return extends Token
  case object OpenedParenthesis extends Token
  case object ClosedParenthesis extends Token
  case object OpenedBrace extends Token
  case object ClosedBrace extends Token

  case object ReturnLine extends Token
  case object Space extends Token
  case object Empty extends Token // Will be mapped to an empty string

  case object Ptr extends Token // Pointer representation will be mapped to '*'
  case object Arrow extends Token
  case object StructTk extends Token

  case object If extends Token
  case object Else extends Token

  // Useful C library functions 
  case object Malloc extends Token
  case object Strlen extends Token
  case object Strcpy extends Token
  case object Strcat extends Token
  case object SizeOf extends Token

  case object CError extends Token
  case class Comment(msg:String) extends Token
  
  // Code represents a sequence of tokens
  case class Code(tokens: List[Token]) {
    def <->(i: Token) = Code(tokens :+ Space :+ i) // Concatenation with space
    def <>(i: Token) = Code(tokens :+ i)// Direct concatenation
    def <->(other: Code) = Code(this.tokens ++ List(Space) ++ other.tokens)// Concatenation with space
    def <>(other: Code) = Code(this.tokens ++ other.tokens) // Direct concatenation
  }

  // Useful implicit conversions to construct Code objects
  implicit def t2c(i: Token): Code = Code(List(i))
  implicit def ts2c(is: List[Token]): Code = Code(is)
  implicit def cs2c(cs: List[Code]): Code = Code(cs flatMap (_.tokens))
}
