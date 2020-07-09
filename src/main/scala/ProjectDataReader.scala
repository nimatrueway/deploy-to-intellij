import java.io.{FileInputStream, InputStream}
import java.nio.file.{Files, Path}

import javax.xml.parsers.DocumentBuilderFactory
import kantan.xpath._
import kantan.xpath.implicits._
import org.slf4j.LoggerFactory

import scala.jdk.CollectionConverters._
import scala.util.chaining._

object ProjectDataReader {
  private val logger = LoggerFactory.getLogger("Reader")
  def read(classpathStream: InputStream, projectStream: InputStream, launchStream: InputStream): ProjectData = {
    try {
      val builder = DocumentBuilderFactory.newInstance.newDocumentBuilder
      val project = builder.parse(projectStream)
      val classpath = builder.parse(classpathStream)
      val launch = builder.parse(launchStream)
      logger.debug("Parsing..")

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
      logger.debug("Project name: '{}'", projectName)

      val classpath1 = classpath.evalXPath[List[String]](xp"/classpath/classpathentry[@kind='lib']/@path").toTry.get
      logger.debug("Classpath libraries: '{}' found.", classpath1.length)

      val classpath2 = {
        launchListQuery("org.eclipse.jdt.launching.CLASSPATH").flatMap(
          _.evalXPath[List[String]](xp"/runtimeClasspathEntry/@externalArchive").getOrElse(Nil)
        )
      }
      logger.debug("Classpath extra libraries: '{}' found.", classpath2.length)

      val vmArguments = launchStringQuery("org.eclipse.jdt.launching.VM_ARGUMENTS")
      logger.debug("Launch configuration: vm arguments = {}.", vmArguments)

      val programArguments = launchStringQuery("org.eclipse.jdt.launching.PROGRAM_ARGUMENTS")
      logger.debug("Program arguments for launching: {}.", programArguments)

      val mainClass = launchStringQuery("org.eclipse.jdt.launching.MAIN_TYPE")
      logger.debug("Launch configuration: main class = {}.", mainClass)

      val envVariables = launchMapQuery("org.eclipse.debug.core.environmentVariables")
      logger.debug("Launch configuration: environment variables = {}", envVariables.mkString("[", ", ", "]"))

      val workingDir = launchStringQuery("org.eclipse.jdt.launching.WORKING_DIRECTORY")
      logger.debug("Launch configuration: working directory = {}", workingDir)

      ProjectData(
        projectName = projectName,
        mainClass = mainClass,
        environmentVariables = envVariables,
        workingDirectory = workingDir,
        classpath = classpath2 ++ classpath1,
        vmArguments = ArgumentTokenizer.tokenize(vmArguments).asScala.toList,
        programArguments = ArgumentTokenizer.tokenize(programArguments).asScala.toList
      )
        .tap { _ => logger.info("Successfully parsed project files.") }

    } finally {
      classpathStream.close()
      projectStream.close()
      launchStream.close()
    }
  }
  def read(root: Path): ProjectData = {
    logger.info("Looking for eclipse project configuration: {}", root.toAbsolutePath)

    val project: Path = root.resolve(".project")
    if (project.toFile.exists())
      logger.debug("Found project file: {}", project.toAbsolutePath)
    else
      throw XmlReaderException("No project file was found!")

    val classpath: Path = root.resolve(".classpath")
    if (classpath.toFile.exists())
      logger.debug("Found classpath file: {}", classpath.toAbsolutePath)
    else
      throw XmlReaderException("No classpath file was found!")

    val launch: Path = {
      val allLaunchFiles = Files.newDirectoryStream(root, "*.launch").iterator()
      if (allLaunchFiles.hasNext)
        allLaunchFiles.next()
          .tap { launchFile => logger.debug("Found launch file: {}", launchFile.toAbsolutePath) }
      else
        throw XmlReaderException("No launch file was found!")
    }
    read(
      new FileInputStream(classpath.toFile),
      new FileInputStream(project.toFile),
      new FileInputStream(launch.toFile),
    )
  }
}

case class XmlReaderException(msg: String, cause: Exception = null) extends Exception(msg, cause)
