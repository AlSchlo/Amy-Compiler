package amyc
package cgen

import ast.Identifier
import c.Function
import c.Tokens.{Identifier => CIdentifier, _}
import amyc.ast.SymbolicTreeModule.{IntType => AmyIntType, BooleanType => AmyBooleanType, StringType => AmyStringType, UnitType => AmyUnitType, ClassType => AmyClassType, Type}

// Utilities for CodeGen
object Utils {

  // Imports that are prepended at the start of any generated C file
  // For the sake of simplicity, we will always import all these libraries even when not used
  val defaultImports: List[String] = List("<stdint.h>", "<stdlib.h>", "<string.h>", "\"std.h\"", "\"error.h\"")

  // C functions that are factory ready (check error.h and std.h)
  val builtInFunctions: Set[String] = Set(
    "Std_printInt",
    "Std_printString",
    "Std_digitToString",
    "Std_readInt",
    "Std_readString",
    "Error_error"
  )

  // A globally unique name for C variables (see in CodeGen)
  def fullName(owner: Identifier, df: Identifier): String = owner.name + "_" + df.name

  // A fresh label name
  def getFreshLabel(name: String = "label") = {
    Identifier.fresh(name).fullName
  }

  // Transform a TreeModule Type into a C Code
  // ClassType are transform to void pointers (see the genericity trick)
  def typeToToken(tpe: Type): Code = {
    tpe match {
      case AmyIntType => IntType
      case AmyBooleanType => IntType
      case AmyStringType => StringType
      case AmyUnitType => IntType 
      case AmyClassType(qName) => VoidType <> Ptr
    }
  }
}
