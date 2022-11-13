package amyc.cgen

import amyc.c.CFile
import amyc.utils.{Context, Pipeline, Env}
import scala.sys.process._
import java.io._

// Creates the IO file.c in the cout dir (last stage of our pipeline)
object CodePrinter extends Pipeline[CFile, Unit]{
  def run(ctx: Context)(f: CFile) = {
    val outDirName = "cout"

    def pathWithExt(ext: String) = s"$outDirName/${nameWithExt(ext)}"
    def nameWithExt(ext: String) = s"${f.name}.$ext"

    val outDir = new File(outDirName)
    if (!outDir.exists()) {
      outDir.mkdir()
    }

    f.writeCText(pathWithExt("c"))
  }
}
