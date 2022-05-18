ThisBuild / version := "0.3.0"

ThisBuild / scalaVersion := "2.13.8"

val json4sVersion = "4.0.5"

lazy val root = (project in file("."))
  .settings(
    name := "TeamResults",

    libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "2.1.0",
    libraryDependencies += "org.json4s" %% "json4s-native" % json4sVersion,
    libraryDependencies += "org.json4s" %% "json4s-xml" % json4sVersion,
    libraryDependencies += "org.json4s" %% "json4s-ext" % json4sVersion
  )
