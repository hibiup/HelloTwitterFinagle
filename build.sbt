lazy val dependencies = new {
    val scalaTestVersion = "3.0.5"
    val logBackVersion = "1.2.3"
    val scalaLoggingVersion = "3.9.2"
    val finagleVersion = "19.4.0"
    val catsVersion = "1.6.0"
    val catsEffectVersion = "1.3.0"
    val finchVersion = "0.28.0"
    val nettyVersion = "3.10.6.Final"

    // Test
    val scalaTest = "org.scalatest" %% "scalatest" % scalaTestVersion % Test
    // Cats
    val cats = "org.typelevel" %% "cats-core" % catsVersion
    val catsEffect = "org.typelevel" %% "cats-effect" % catsEffectVersion
    // Logging
    val logback = "ch.qos.logback" % "logback-classic" % logBackVersion
    val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion
    // Finagle
    val finagle = "com.twitter" %% "finagle-http" % finagleVersion
    // Finch
    val finch = "com.github.finagle" %% "finch-core" % finchVersion
    // Netty for request codec
    val netty = "io.netty" % "netty" % nettyVersion
}

lazy val root = (project in file("."))
    .settings(
        organization := "com.hibiup",
        name := "Finagle",
        version := "0.1",
        scalaVersion := "2.12.8",
        libraryDependencies ++= Seq(
            dependencies.scalaTest,
            dependencies.logback,
            dependencies.scalaLogging,
            dependencies.finagle,
            dependencies.cats,
            dependencies.catsEffect,
            dependencies.finch,
            dependencies.netty,
        ),
        scalacOptions ++= Seq(
            "-Ypartial-unification"
        )
    )
