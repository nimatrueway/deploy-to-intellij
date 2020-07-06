Build native executable
=======================
See the official documentation in [here](https://www.graalvm.org/docs/reference-manual/native-image/#prerequisites).

- Windows: install visual studio community to provide build-tools and c++ compiler for native-image. [see](https://gist.github.com/sogaiu/e079cd770051685c46ab24b6658effcf)

```
choco install visualstudio2017community --version 15.9.17.0 --no-progress --package-parameters "--add Microsoft.VisualStudio.Component.VC.Tools.ARM64 --add Microsoft.VisualStudio.Component.VC.CMake.Project"
```

- Windows & Linux

```
sdk install sbt
sdk install 20.1.0.r11-grl
# add to PATH: '$sbt/bin', '$graalvm/bin' 
sbt "graalvm-native-image:packageBin"
```