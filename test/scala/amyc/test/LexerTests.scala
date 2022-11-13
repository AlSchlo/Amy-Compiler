package amyc.test

import amyc.parsing._
import org.junit.Test

class LexerTests extends TestSuite {
  val pipeline = Lexer andThen DisplayTokens

  val baseDir = "lexer"

  val outputExt = "txt"

  @Test def testKeywords = shouldOutput("Keywords")

  @Test def testSingleAmp = shouldFail("SingleAmp")

  @Test def testWhiteSpace = shouldOutput("Whitespace")

  @Test def testCommentClosedTwice = shouldOutput("CommentClosedTwice")

  @Test def testUnclosedComment3 = shouldFail("UnclosedComment3")

  // My tests

  @Test def testUnclosedString = shouldFail("UnclosedString")

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

  @Test def testOverflow = shouldFail("Overflow")
  
  @Test def testStd = shouldOutput("Std")

}
