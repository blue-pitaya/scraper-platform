import Dependencies._

ThisBuild / scalaVersion     := "2.13.8"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

lazy val akkaHttpVersion = "10.2.7"
lazy val akkaVersion    = "2.6.9"

lazy val root = (project in file("."))
  .settings(
    name := "scraper",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http"                % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json"     % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-actor-typed"         % akkaVersion,
      "com.typesafe.akka" %% "akka-stream"              % akkaVersion,
      "com.typesafe.akka" %% "akka-stream-typed" % akkaVersion,
      "com.typesafe.akka" %% "akka-persistence-typed"   % akkaVersion,
      "com.typesafe.akka" %% "akka-persistence-cassandra" % "1.0.5",
      "com.typesafe.akka" %% "akka-persistence" % akkaVersion,
      "com.typesafe.akka" %% "akka-persistence-query" % akkaVersion,
      "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion,
      "com.softwaremill.akka-http-session" %% "core" % "0.7.0",

      "net.ruippeixotog" %% "scala-scraper" % "2.2.1",
      "ch.qos.logback"    % "logback-classic"           % "1.2.3",
      "io.lemonlabs" %% "scala-uri" % "4.0.2",

      "com.typesafe.akka" %% "akka-http-testkit"        % akkaHttpVersion % Test,
      "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion     % Test,
      "org.scalatest"     %% "scalatest"                % "3.1.4"         % Test,
      "com.github.tototoshi" %% "scala-csv" % "1.3.10",

      "org.tpolecat" %% "doobie-core"      % "1.0.0-RC1",
      "org.tpolecat" %% "doobie-postgres"  % "1.0.0-RC1",
      "org.typelevel" %% "cats-effect" % "3.3.11",
    )

  )

  scalacOptions ++= Seq("-deprecation", "-feature")

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
