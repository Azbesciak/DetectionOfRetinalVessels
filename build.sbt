name := "DetectionOfRetinalVessels"

version := "0.1"

scalaVersion := "2.12.5"

libraryDependencies += "org.scalafx" %% "scalafx" % "8.0.144-R12"
libraryDependencies += "org.scalafx" %% "scalafxml-core-sfx8" % "0.4"
libraryDependencies += "com.jfoenix" % "jfoenix" % "9.0.3"
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.5.12"
libraryDependencies += "com.twelvemonkeys.imageio" % "imageio-core" % "3.3.2"
libraryDependencies += "com.twelvemonkeys.imageio" % "imageio-tiff" % "3.3.2"

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
