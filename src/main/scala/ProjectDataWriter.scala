import java.io.{FileOutputStream, OutputStreamWriter, Writer}
import java.nio.file.{Files, Path}
import java.security.MessageDigest
import java.util.Base64

import xml._

object ProjectDataWriter {
  def write(projectData: ProjectData,
            miscStream: Writer,
            modulesStream: Writer,
            projectImlStream: Writer,
            workspaceStream: Writer,
            mendixRuntimeStream: Writer,
            userLibStream: Writer): Unit = {
    val (userLibs, mendixLibs) = projectData.classpath.partition(_.startsWith("userlib/"))
    val projectId = new String(Base64.getEncoder.encode(MessageDigest.getInstance("MD5").digest(projectData.projectName.getBytes)))
    val jdkVersion = Interpreter.findJdkVersion(projectData)
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
    miscStream.write(misc.render(jdkVersion.toString).body)
    projectImlStream.write(project.render().body)
    modulesStream.write(modules.render(projectData.projectName).body)
    userLibStream.write(lib.render(
      libraryName = "userlib",
      jars = userLibs.map(path => s"$$PROJECT_DIR$$/$path")
    ).body)
    mendixRuntimeStream.write(lib.render(
      libraryName = "mendix-runtime",
      jars = mendixLibs
    ).body)
    miscStream.close()
    modulesStream.close()
    projectImlStream.close()
    workspaceStream.close()
    userLibStream.close()
    mendixRuntimeStream.close()
  }

  def write(projectData: ProjectData, root: Path): Unit = {
    val ideaFolder = root.resolve(".idea")
    val librariesFolder = ideaFolder.resolve("libraries")
    val misc = ideaFolder.resolve("misc.xml")
    val modules = ideaFolder.resolve("modules.xml")
    val projectIml = ideaFolder.resolve(s"${projectData.projectName}.iml")
    val workspace = ideaFolder.resolve("workspace.xml")
    val mendixRuntime = librariesFolder.resolve("mendix_runtime.xml")
    val userLib = librariesFolder.resolve("userlib.xml")
    if (!ideaFolder.toFile.exists())
      Files.createDirectory(ideaFolder)
    if (!librariesFolder.toFile.exists())
      Files.createDirectory(librariesFolder)
    Files.deleteIfExists(misc)
    Files.deleteIfExists(modules)
    Files.deleteIfExists(projectIml)
    Files.deleteIfExists(workspace)
    Files.deleteIfExists(mendixRuntime)
    Files.deleteIfExists(userLib)
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
