import java.nio.file.Paths

object Main {
  def main(args: Array[String]): Unit = {
    val projectRoot = Paths.get(args.headOption.getOrElse("."))
    val projectData = Reader.read(projectRoot)
    ProjectDataWriter.write(projectData, projectRoot)
  }
}
