case class ProjectData(projectName: String,
                       environmentVariables: Map[String, String],
                       classpath: List[String],
                       mainClass: String,
                       vmArguments: List[String],
                       programArguments: List[String],
                       workingDirectory: String)