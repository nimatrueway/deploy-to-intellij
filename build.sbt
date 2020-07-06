enablePlugins(GraalVMNativeImagePlugin)

name := "eclipse-to-intellij"
version := "0.1"
scalaVersion := "2.13.3"

graalVMNativeImageOptions := List("--no-fallback", "--initialize-at-build-time")

libraryDependencies += "com.nrinaudo" %% "kantan.xpath" % "0.5.2"
libraryDependencies += "com.gu" %% "spy" % "0.1.1"