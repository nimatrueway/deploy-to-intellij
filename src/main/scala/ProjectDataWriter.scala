import java.io.{FileOutputStream, OutputStreamWriter, Writer}
import java.nio.file.{Files, Path}
import java.security.MessageDigest
import java.util.Base64

import org.slf4j.LoggerFactory
import xml._

object ProjectDataWriter {
  private val logger = LoggerFactory.getLogger("Writer")
  def write(projectData: ProjectData,
            miscStream: Writer,
            modulesStream: Writer,
            projectImlStream: Writer,
            workspaceStream: Writer,
            mendixRuntimeStream: Writer,
            userLibStream: Writer): Unit = {
    try {
      val projectId = new String(Base64.getEncoder.encode(MessageDigest.getInstance("MD5").digest(projectData.projectName.getBytes)))
      logger.debug("Project id will be '{}'.", projectId)

      val jdkVersion = Interpreter.findJdkVersion(projectData)
      logger.debug("Project jdk version will be '{}'.", jdkVersion)

      projectImlStream.write(project.render().body)
      logger.debug("Created project iml file.")

      modulesStream.write(modules.render(projectData.projectName).body)
      val (userLibs, mendixLibs) = projectData.classpath.partition(_.startsWith("userlib/"))
      userLibStream.write(lib.render(
        libraryName = "userlib",
        jars = userLibs.map(path => s"$$PROJECT_DIR$$/$path")
      ).body)
      mendixRuntimeStream.write(lib.render(
        libraryName = "mendix-runtime",
        jars = mendixLibs
      ).body)
      logger.debug("Created project module files and libraries.")

      workspaceStream.write(
        workspace.render(
          projectId = projectId,
          envVars = projectData.environmentVariables,
          mainClass = projectData.mainClass,
          projectName = projectData.projectName,
          programArgs = projectData.programArguments.mkString("\"", "\" \"", "\""),
          vmArgs = projectData.vmArguments.mkString("\"", "\" \"", "\"")
        ).body
      )
      logger.debug("Created project workspace file and launch configurations.")

      miscStream.write(misc.render(jdkVersion.toString).body)
      logger.debug("Created project miscellaneous file.")

      logger.info("Successfully created intellij project files.")
    } finally {
      miscStream.close()
      modulesStream.close()
      projectImlStream.close()
      workspaceStream.close()
      userLibStream.close()
      mendixRuntimeStream.close()
    }
  }

  def write(projectData: ProjectData, root: Path): Unit = {
    logger.info("Storing intellij project configuration: {}", root.toAbsolutePath)

    val ideaFolder = root.resolve(".idea")
    val librariesFolder = ideaFolder.resolve("libraries")
    val misc = ideaFolder.resolve("misc.xml")
    val modules = ideaFolder.resolve("modules.xml")
    val projectIml = ideaFolder.resolve(s"${projectData.projectName}.iml")
    val workspace = ideaFolder.resolve("workspace.xml")
    val mendixRuntime = librariesFolder.resolve("mendix_runtime.xml")
    val userLib = librariesFolder.resolve("userlib.xml")

    if (!ideaFolder.toFile.exists()) {
      Files.createDirectory(ideaFolder)
      logger.debug("Created '{}' folder.", ideaFolder)
    }

    if (!librariesFolder.toFile.exists()) {
      Files.createDirectory(librariesFolder)
      logger.debug("Created `{}` folder.", librariesFolder)
    }

    if (Files.deleteIfExists(misc))
      logger.debug("Deleted `{}` file.", misc)

    if (Files.deleteIfExists(modules))
      logger.debug("Deleted `{}` file.", modules)

    if (Files.deleteIfExists(projectIml))
      logger.debug("Deleted `{}` file.", projectIml)

    if (Files.deleteIfExists(workspace))
      logger.debug("Deleted `{}` file.", workspace)

    if (Files.deleteIfExists(mendixRuntime))
      logger.debug("Deleted `{}` file.", mendixRuntime)

    if (Files.deleteIfExists(userLib))
      logger.debug("Deleted `{}` file.", userLib)

    write(
      projectData,
      new OutputStreamWriter(new FileOutputStream(misc.toFile)),
      new OutputStreamWriter(new FileOutputStream(modules.toFile)),
      new OutputStreamWriter(new FileOutputStream(projectIml.toFile)),
      new OutputStreamWriter(new FileOutputStream(workspace.toFile)),
      new OutputStreamWriter(new FileOutputStream(mendixRuntime.toFile)),
      new OutputStreamWriter(new FileOutputStream(userLib.toFile)),
    )
  }
}
