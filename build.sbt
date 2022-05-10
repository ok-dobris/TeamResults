ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

val weeVersion = "1.7.2"

lazy val root = (project in file("."))
  .settings(
    name := "TeamResults",

    libraryDependencies += "com.rallyhealth" %% "weexml-v1" % weeVersion,
    libraryDependencies += "com.rallyhealth" %% "weepickle-v1" % weeVersion,
  )
