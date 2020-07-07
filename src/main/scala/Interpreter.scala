import java.nio.file.{Path, Paths}

object Interpreter {
  def findJdkVersion(projectData: ProjectData): Int = {
    findMendixVersion(projectData).split("\\.").head.toInt match {
      case 6 => 7
      case 7 => 8
      case 8 => 11
      case _ => 8
    }
  }
  def findMendixPath(projectData: ProjectData): Path =
    Paths.get(projectData.environmentVariables("MX_INSTALL_PATH"))
  def findMendixVersion(projectData: ProjectData): String =
    findMendixPath(projectData).getFileName.toString

}
