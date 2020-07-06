enablePlugins(GraalVMNativeImagePlugin)

name := "eclipse-to-intellij"
version := "0.1"
scalaVersion := "2.13.3"

graalVMNativeImageOptions := List("--no-fallback", "--initialize-at-build-time")

libraryDependencies += "com.nrinaudo" %% "kantan.xpath" % "0.5.2"
libraryDependencies += "com.gu" %% "spy" % "0.1.1"

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