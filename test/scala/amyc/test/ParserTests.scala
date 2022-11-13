package amyc.test

import amyc.parsing._
import org.junit.Test

class ParserTests extends TestSuite with amyc.MainHelpers {
  val pipeline = Lexer andThen Parser andThen treePrinterN("")

  val baseDir = "parser"

  val outputExt = "scala"

  @Test def testLL1 = {
    assert(Parser.program.isLL1)
  }


  // Given tests

  @Test def testEmpty = shouldOutput("Empty")
  
  @Test def testLiterals = shouldOutput("Literals")

  @Test def testMatchScrutinee = shouldOutput("MatchScrutinee")

  @Test def testIfCondition = shouldOutput("IfCondition")

  @Test def testChainedMatch = shouldOutput("ChainedMatch")

  @Test def testCommentClosedTwice = shouldFail("CommentClosedTwice")

  @Test def testIfPrecedence = shouldFail("IfPrecedence")

  @Test def testEmptyFile = shouldFail("EmptyFile")

  // My tests
  
  @Test def testArithmetic = shouldOutput("Arithmetic")

  @Test def testFactorial = shouldOutput("Factorial")

  @Test def testHanoi = shouldOutput("Hanoi")

  @Test def testHello = shouldOutput("Hello")

  @Test def testHelloInt = shouldOutput("HelloInt")

  @Test def testPrinting = shouldOutput("Printing")

  @Test def testTestLists = shouldOutput("TestLists")

  @Test def testList = shouldOutput("List")

  @Test def testOption = shouldOutput("Option")

  @Test def testHumanClass = shouldOutput("HumanClass")

  @Test def testMatchList = shouldOutput("MatchList")
  
  @Test def testStd = shouldOutput("Std")

  @Test def testWeirdMatch = shouldOutput("WeirdMatch")

  @Test def testEdgeCaseCodeForParser = shouldOutput("EdgeCaseCodeForParser")

  @Test def testBadVal = shouldFail("BadVal")
  
  @Test def testBadValBis = shouldFail("BadValBis")

  @Test def testBadUnary = shouldFail("BadUnary")

  @Test def testBadMatch = shouldFail("BadMatch")

  @Test def testMotherOfAllParserTests = shouldOutput("MotherOfAllParserTests")
}