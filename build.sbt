name := "states_machine"
organization := "com.test"
version := "1.0.0-SNAPSHOT"
scalaVersion := "2.13.1"

resolvers += Resolver.jcenterRepo

val akkaHttp = "10.1.11"
val akka = "2.6.3"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % akkaHttp,
  "com.typesafe.akka" %% "akka-stream" % akka,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttp,
  "com.typesafe.akka" %% "akka-slf4j" % akka,
  "org.scalaj" %% "scalaj-http" % "2.4.2",
  "ch.qos.logback"    % "logback-classic" % "1.2.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",

  //test libraries
  "com.typesafe.akka" %% "akka-testkit" % akka,
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttp,
  "org.scalactic" %% "scalactic" % "3.0.8",
  "org.scalatest" %% "scalatest" % "3.0.8" % "test"
)

parallelExecution in Test := false