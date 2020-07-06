import java.nio.file.{Files, Path}

import javax.xml.parsers.DocumentBuilderFactory
import kantan.xpath._
import kantan.xpath.implicits._

import scala.jdk.CollectionConverters._

class Reader(root: Path) {
  def read(classpathPath: Path, projectPath: Path, launchPath: Path): ProjectData = {
    val builder = DocumentBuilderFactory.newInstance.newDocumentBuilder
    val project = builder.parse(projectPath.toFile)
    val classpath = builder.parse(classpathPath.toFile)
    val launch = builder.parse(launchPath.toFile)

    def compile(xpath: String) =
      implicitly[XPathCompiler].compile(xpath).getOrElse(throw XmlReaderException(s"Invalid Xpath: $xpath"))
    def launchStringQuery(name: String) =
      launch.evalXPath[String](compile(s"/launchConfiguration/stringAttribute[@key='$name']/@value")).toTry.get
    def launchListQuery(name: String) =
      launch.evalXPath[List[String]](compile(s"/launchConfiguration/listAttribute[@key='$name']/listEntry/@value")).toTry.get
    def launchMapQuery(name: String) = {
      implicit val decoder: NodeDecoder[(String, String)] = NodeDecoder.decoder(xp"./@key", xp"./@value")(Tuple2.apply[String, String])
      launch.evalXPath[List[(String, String)]](compile(s"/launchConfiguration/mapAttribute[@key='$name']/mapEntry")).toTry.get.toMap
    }
    val projectName = project.evalXPath[String](xp"/projectDescription/name").toTry.get
    val classpath1 = classpath.evalXPath[List[String]](xp"/classpath/classpathentry[@kind='lib']/@path").toTry.get
    val classpath2 = {
      launchListQuery("org.eclipse.jdt.launching.CLASSPATH").flatMap(
        _.evalXPath[List[String]](xp"/runtimeClasspathEntry/@externalArchive").getOrElse(Nil)
      )
    }
    val vmArguments = launchStringQuery("org.eclipse.jdt.launching.VM_ARGUMENTS")
    val programArguments = launchStringQuery("org.eclipse.jdt.launching.PROGRAM_ARGUMENTS")
    ProjectData(
      projectName = projectName,
      mainClass = launchStringQuery("org.eclipse.jdt.launching.MAIN_TYPE"),
      environmentVariables = launchMapQuery("org.eclipse.debug.core.environmentVariables"),
      workingDirectory = launchStringQuery("org.eclipse.jdt.launching.WORKING_DIRECTORY"),
      classpath = classpath2 ++ classpath1,
      vmArguments = ArgumentTokenizer.tokenize(vmArguments).asScala.toList,
      programArguments = ArgumentTokenizer.tokenize(programArguments).asScala.toList
    )
  }
  def read(): ProjectData = {
    val project: Path = root.resolve(".project")
    val classpath: Path = root.resolve(".classpath")
    val launch: Path = {
      val allLaunchFiles = Files.newDirectoryStream(root, "*.launch").iterator()
      if (allLaunchFiles.hasNext) allLaunchFiles.next()
      else throw XmlReaderException("No launch file was found!")
    }
    read(
      classpath,
      project,
      launch,
    )
  }
}

case class XmlReaderException(msg: String, cause: Exception = null) extends Exception(msg, cause)
