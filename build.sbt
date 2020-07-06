enablePlugins(GraalVMNativeImagePlugin)

name := "eclipse-to-intellij"
version := "0.1"
scalaVersion := "2.13.3"

graalVMNativeImageOptions := List("--no-fallback", "--initialize-at-build-time")