import java.io.{FileOutputStream, OutputStreamWriter, Writer}
import java.nio.file.{Files, Path}
import java.security.MessageDigest
import java.util
import java.util.Base64

import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver

import scala.jdk.CollectionConverters._

object ProjectDataWriter {
  private val ctx = new Context()
  private val templateEngine = new TemplateEngine
  private val resolver = new ClassLoaderTemplateResolver()
  resolver.setPrefix("project-template/")
  templateEngine.setTemplateResolver(resolver)

  def write(projectData: ProjectData,
            miscStream: Writer,
            modulesStream: Writer,
            projectImlStream: Writer,
            workspaceStream: Writer,
            mendixRuntimeStream: Writer,
            userLibStream: Writer): Unit = {
    val (userLibs, mendixLibs) = projectData.classpath.partition(_.startsWith("userlib/"))
    val projectId = new String(Base64.getEncoder.encode(MessageDigest.getInstance("MD5").digest(projectData.projectName.getBytes)))
    ctx.setVariable("jdk_version", Interpreter.findJdkVersion(projectData))
    ctx.setVariable("project_id", projectId)
    ctx.setVariable("project_name", projectData.projectName)
    ctx.setVariable("working_directory", projectData.workingDirectory)
    ctx.setVariable("main_class", projectData.mainClass)
    ctx.setVariable("program_args", projectData.programArguments.mkString("\"", "\" \"", "\""))
    ctx.setVariable("vm_args", projectData.vmArguments.mkString("\"", "\" \"", "\""))
    ctx.setVariable("env_vars", new util.HashMap(projectData.environmentVariables.asJava))
    ctx.setVariable("classpath_user_libs", userLibs.map(path => s"jar://$$PROJECT_DIR$$/$path!/").asJava)
    ctx.setVariable("classpath_mendix_libs", mendixLibs.map(path => s"jar://$path!/").asJava)
    templateEngine.process("misc.xml", ctx, miscStream)
    templateEngine.process("modules.xml", ctx, modulesStream)
    templateEngine.process("project.xml", ctx, projectImlStream)
    templateEngine.process("workspace.xml", ctx, workspaceStream)
    templateEngine.process("libraries/userlib.xml", ctx, userLibStream)
    templateEngine.process("libraries/mendix_runtime.xml", ctx, mendixRuntimeStream)
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
