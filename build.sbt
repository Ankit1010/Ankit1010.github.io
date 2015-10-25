import sbt.Keys._

val autoimports = ""

lazy val ghpage = (project in file(".")).
  enablePlugins(ScalaJSPlugin).
  settings(
    organization := "org.ankits",
    version := "0.1.0",
    scalaVersion := "2.11.7",
    libraryDependencies ++= Seq(
			"com.lihaoyi" %%% "scalatags" % "0.5.3"
		),
    initialCommands in console := autoimports,
    artifactPath in (Compile, fullOptJS) := file(".") / "static"/ "js" / ((moduleName in fullOptJS).value + "-opt.js")
  )
