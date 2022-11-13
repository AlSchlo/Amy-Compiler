package amyc.c
import Tokens.Code
import amyc.ast.SymbolicTreeModule.ParamDef
import amyc.ast.SymbolicTreeModule.Type

/**
* Representation of a printable C function.
* @param fullName The entire name of the function (prepended with the module name)
* @param params The list of its parameters
* @param retType The return type
* @param isMain True if this function is main false else
* @param code The Code of the function
*/
class Function(val fullName: String, val params: List[ParamDef], val retType: Type, val isMain: Boolean, val code: Code) {
  override def toString: String = CFilePrinter(this)
}

