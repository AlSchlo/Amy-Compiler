lazy val amyc = (project in file("."))
  .disablePlugins(plugins.JUnitXmlReportPlugin)
  .enablePlugins(StudentPlugin)
  .settings(
    name := "amyc",

    version := "1.7",
    organization := "ch.epfl.lara",
    scalaVersion := "3.0.1",

    Compile / scalaSource := baseDirectory.value / "src",
    scalacOptions ++= Seq("-feature"),

    Test / scalaSource := baseDirectory.value / "test" / "scala",
    Test / parallelExecution := false,
    libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % "test",
    testOptions += Tests.Argument(TestFrameworks.JUnit, "-v")
  )
