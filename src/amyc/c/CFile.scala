package amyc
package c

// A printable C file representation
case class CFile(name: String, imports: List[String], functions: List[Function], structs: List[Struct]) {

  import java.io.{File, FileWriter}

  def writeCText(fileName: String) = {
    val fw = new FileWriter(new File(fileName))
    fw.write(CFilePrinter(this))
    fw.flush()
  }
}