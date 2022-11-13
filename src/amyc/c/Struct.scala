package amyc.c
import amyc.ast.SymbolicTreeModule.Type


/**
* Representation of a printable C struct.
* @param fullName The entire name of the struct (prepended with the module name)
* @param index The index of the constructor of the struct (for pattern matching)
* @param params Parameter of the given constructor
*/
class Struct(val fullName: String, val index: Int, val params: List[(Type, String)]) {
  override def toString: String = CFilePrinter(this)
}

