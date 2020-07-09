import java.nio.file.Paths

object Main {
  def main(args: Array[String]): Unit = {
    val projectRoot = Paths.get(args.headOption.getOrElse("."))
    val projectData = ProjectDataReader.read(projectRoot)
    ProjectDataWriter.write(projectData, projectRoot)
    Console.println("Press RETURN to exit...")
    Console.in.read()
  }
}
