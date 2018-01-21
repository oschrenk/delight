name := "delight"
organization := "com.oschrenk"
version := "0.1.0"

scalaVersion := "2.12.4"

resolvers += Resolver.bintrayRepo("oschrenk", "maven")
resolvers ++= Seq(
  "Sonatype OSS Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"
)

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-java8",
  "io.circe" %% "circe-parser"
).map(_ % "0.9.1")

libraryDependencies ++= Seq(
  "org.platzhaltr.parsing" %% "datr-scala" % "0.1.0",
  "com.github.scopt" %% "scopt" % "3.7.0",
  "com.github.pathikrit" %% "better-files" % "3.4.0",
  "com.typesafe" % "config" % "1.3.2",
  "pt.davidafsilva.apple" % "jkeychain" % "1.0.0",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "net.ruippeixotog" %% "scala-scraper" % "2.1.0",
  "org.scalatest" %% "scalatest" % "3.0.4" % "test"
)

scalacOptions ++= Seq(
    "-target:jvm-1.8",
    "-deprecation",
    "-encoding", "UTF-8",
    "-feature",
    "-language:existentials",
    "-language:higherKinds",
    "-language:implicitConversions",
    "-language:experimental.macros",
    "-unchecked",
    //"-Ywarn-unused-import",
    "-Ywarn-nullary-unit",
    "-Xfatal-warnings",
    "-Xlint",
    //"-Yinline-warnings",
    "-Ywarn-dead-code",
    "-Xfuture")

initialCommands := "import com.oschrenk.delight._"

