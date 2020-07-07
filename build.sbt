enablePlugins(GraalVMNativeImagePlugin)

name := "eclipse-to-intellij"
version := "0.1"
scalaVersion := "2.13.3"

graalVMNativeImageOptions := {
  val prjRoot = baseDirectory.value
  "--no-fallback" ::
    "--initialize-at-build-time" ::
    s"-H:ResourceConfigurationFiles=${prjRoot}/native-image/resource-config.json" ::
    s"-H:ReflectionConfigurationFiles=${prjRoot}/native-image/reflect-config.json" ::
    "--allow-incomplete-classpath" ::
//   for debugging
//    "-H:+ReportExceptionStackTraces" ::
    Nil
}

// read/write libraries
libraryDependencies += "com.nrinaudo" %% "kantan.xpath" % "0.5.2"
libraryDependencies += "org.thymeleaf" % "thymeleaf" % "3.0.11.RELEASE"
libraryDependencies += "org.slf4j" % "slf4j-nop" % "1.7.25"
// test libraries
libraryDependencies += "com.gu" %% "spy" % "0.1.1" % Test
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.0" % Test

graalVMNativeImageCommand := { // sbt-native-packager workaround to find "native-image.cmd" in windows
                               // see: https://www.scala-sbt.org/sbt-native-packager/formats/graalvm-native-image.html#settings
  (if (System.getProperty("os.name").startsWith("Windows"))
     System
       .getenv("PATH")
       .split(";")
       .map(path => (file(path) / "native-image.cmd").absolutePath)
       .find(file(_).exists())
   else None).getOrElse(graalVMNativeImageCommand.value)
}