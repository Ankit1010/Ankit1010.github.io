seq(Revolver.settings: _*) //for sbt-revolver

enablePlugins(ScalaJSPlugin)

val autoimports = ""

lazy val ghpage = (project in file(".")).
  settings(
    organization := "org.ankits",
    version := "0.1.0",
    scalaVersion := "2.11.7",
    libraryDependencies ++= Seq(
			"com.lihaoyi" %%% "scalatags" % "0.5.3"
		),
    initialCommands in console := autoimports
  )
