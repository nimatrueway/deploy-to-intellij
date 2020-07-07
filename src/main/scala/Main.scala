import java.nio.file.Paths

object Main {
  def main(args: Array[String]): Unit = {
    val projectData = Reader.read(Paths.get(args.headOption.getOrElse(".")))
    println(projectData)
  }
}
