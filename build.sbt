lazy val dependencies = new {
    val scalaTestVersion = "3.0.5"
    val logBackVersion = "1.2.3"
    val scalaLoggingVersion = "3.9.2"
    val finagleVersion = "19.4.0"

    // Test
    val scalaTest = "org.scalatest" %% "scalatest" % scalaTestVersion % Test
    // Logging
    val logback = "ch.qos.logback" % "logback-classic" % logBackVersion
    val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion
    // Finagle
    val finagle = "com.twitter" %% "finagle-http" % finagleVersion
}

lazy val root = (project in file(".")).
        settings(
            organization := "com.hibiup",
            name := "Finagle",
            version := "0.1",
            scalaVersion := "2.12.8",
            libraryDependencies ++= Seq(
                dependencies.scalaTest,
                dependencies.logback,
                dependencies.scalaLogging,
                dependencies.finagle,
            )
        )
