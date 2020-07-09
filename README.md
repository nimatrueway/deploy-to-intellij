Mendix Eclipse to IntelliJ
==========================
Mendix has "Deploy to Eclipse" feature which creates necessary eclipse project configuration files to enable Mendix developers to run their applications or develop "Java Actions" using Eclipse as a proper integrated development environment. Eclipse days are far behind us and Jetbrian IntelliJ is much more popular nowadays.

This tiny standalone tool will help you to generate IntelliJ project configuration based on eclipse project configuration generated by Mendix.

The other motivation for me was to play with graalvm's native-image utility and use it in action. You can find the standalone native executables of this tool in release section.

Development
=======================

Prepare the environment
-----------------------

1 - Install SBT. You may use [sdkman](https://sdkman.io/) for this task. 

```
sdk install sbt
# Add sbt bin directory to your PATH: '$sbt/bin'
```

2 - Install jdk-11 based GraalVM.

```
sdk install 20.1.0.r11-grl
# Add graalvm bin directory to your PATH: '$sbt/bin'
```

3 - [Windows only] install Visual Studio 2017 required for building windows executable. You may use [chocolatey](https://chocolatey.org/) for this task.

```
choco install visualstudio2017community --version 15.9.17.0 --no-progress --package-parameters "--add Microsoft.VisualStudio.Component.VC.Tools.ARM64 --add Microsoft.VisualStudio.Component.VC.CMake.Project"
```

IDE preparation (IntelliJ)
-----------------------

Since this project uses [twirl template engine](https://github.com/playframework/twirl) which compiles twirl templates to scala sources using a custom sbt task, you can not use IntelliJ's compiler to compile this project. Alternatively, you can configure IntelliJ to use sbt-shell for compilation in `File | Settings | Build, Execution, Deployment | Build Tools | sbt` by checking `Use sbt shell: for build [x]`.

Create native executable
-----------------------

1 - [Linux/MacOS only]
 
```
./build-exe.sh
```

2 - [Windows only] 

```
build-exe.bat
```