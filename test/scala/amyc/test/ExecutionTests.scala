package amyc.test

import org.junit.Test

abstract class ExecutionTests extends TestSuite {

  val baseDir = "interpreter"

  val outputExt = "txt"

  @Test def testEmptyObject = shouldOutput("EmptyObject")

  @Test def testMinimalError = shouldFail("MinimalError")

  @Test def testArithmetic = shouldOutput(List("Std", "Arithmetic"), "Arithmetic")

  @Test def testFactorial = shouldOutput(List("Std", "Factorial"), "Factorial")

  @Test def testHanoi = shouldOutput(List("Std", "Hanoi"), "Hanoi")

  @Test def testHello = shouldOutput(List("Std", "Hello"), "Hello")

  @Test def testPrinting = shouldOutput(List("Std", "Printing"), "Printing")

  @Test def testLists = shouldOutput(List("Std", "List", "TestLists", "Option"), "TestLists")

  @Test def testMatchList = shouldOutput(List("Std", "List", "MatchList", "Option"), "MatchList")

  @Test def testCaseClasses = shouldOutput(List("Std", "List", "HumanClass", "Option"), "HumanClass")

  @Test def divFail = shouldFail("Div0")

}