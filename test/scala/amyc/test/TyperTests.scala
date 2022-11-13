package amyc.test

import amyc.parsing._
import amyc.ast.{Identifier, SymbolicPrinter}
import amyc.ast.SymbolicTreeModule.Program
import amyc.analyzer.{NameAnalyzer, TypeChecker}
import amyc.utils._
import org.junit.Test

class TyperTests extends TestSuite {
  // We need a unit pipeline
  private def unit[A]: Pipeline[A, Unit] = {
    new Pipeline[A, Unit] {
      def run(ctx: Context)(v: A) = ()
    }
  }

  val pipeline = Lexer andThen Parser andThen NameAnalyzer andThen TypeChecker andThen unit

  val baseDir = "typer"

  val outputExt = "" // No output files for typechecking

  @Test def testArithError1 = shouldFail("ArithError1")

  @Test def testLetError1 = shouldFail("LetError1")

  @Test def testLetError2 = shouldFail("LetError2")
  
  @Test def testOperatorError1 = shouldFail("OperatorError1")

  @Test def testOperatorError2 = shouldFail("OperatorError2")

  @Test def testOperatorError3 = shouldFail("OperatorError3")

  @Test def testSeqError3 = shouldFail("SeqError3")

  @Test def testArithmetic = shouldPass("Arithmetic")

  @Test def testMatchCase = shouldPass(List("List", "Option", "Std", "MatchCase"), "MatchCase")

  @Test def testMatchCase1 = shouldFail(List("List", "Option", "Std", "MatchCase1"), "MatchCase1")

  @Test def testMatchCase2 = shouldFail(List("List", "Option", "Std", "MatchCase2"), "MatchCase2")

  @Test def testMatchCase3 = shouldFail(List("List", "Option", "Std", "MatchCase3"), "MatchCase3")

  @Test def testMatchCase4 = shouldFail(List("List", "Option", "Std", "MatchCase4"), "Match4Case4")

  @Test def testIfCase = shouldPass("IfCase")
  
  @Test def testIfCase1 = shouldFail("IfCase1")

  @Test def testIfCase2 = shouldFail("IfCase2")

  @Test def testCall = shouldPass(List("List", "Option", "Std", "Call"), "Call")
  
  @Test def testCall1 = shouldFail(List("List", "Option", "Std", "Call1"), "Call1")

  @Test def testCall2 = shouldFail(List("List", "Option", "Std", "Call2"), "Call2")

  @Test def testStupidMatch = shouldPass(List("List", "Option", "Std", "StupidMatch"), "StupidMatch")

  @Test def testStupidestMatch = shouldFail(List("List", "Option", "Std", "StupidestMatch"), "StupidestMatch")
  
  // Previous tests

  @Test def testArithmetic2 = shouldPass(List("Std", "Arithmetic2"), "Arithmetic2")

  @Test def testFactorial = shouldPass(List("Std", "Factorial"), "Factorial")

  @Test def testHanoi = shouldPass(List("Std", "Hanoi"), "Hanoi")

  @Test def testHello = shouldPass(List("Std", "Hello"), "Hello")

  @Test def testHelloInt = shouldPass(List("Std", "HelloInt"), "HelloInt")

  @Test def testMatchList = shouldPass(List("List", "Option", "Std", "MatchList"), "MatchList")

  @Test def testHumanClass = shouldPass(List("List", "Std", "Option", "HumanClass"), "HumanClass")

  @Test def testPrinting = shouldPass(List("Std", "Printing"), "Printing")

  @Test def testLists = shouldPass(List("List", "Option", "Std", "TestLists"), "TestLists")

  @Test def testStd = shouldPass("Std")

  @Test def testList = shouldPass(List("List", "Option", "Std"), "List")

  @Test def testOption = shouldPass(List("List", "Option", "Std"), "Option")
}