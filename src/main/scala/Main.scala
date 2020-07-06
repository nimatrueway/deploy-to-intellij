import java.nio.file.Paths

import com.gu.spy._

object Main {
  def main(args: Array[String]): Unit = {
    val reader = new Reader(Paths.get(args.headOption.getOrElse(".")))
    val projectData = reader.read()
    println(projectData.spy)
  }
}
