import com.typesafe.sbt.SbtNativePackager._
import info.pdalpra.jooq.util.sbt.model._

packageArchetype.java_application
name := "agoraphilia-server"
organization := "com.twitter.finatra.example"
version := "2.1.4"
scalaVersion := "2.11.7"
fork in run := true
parallelExecution in ThisBuild := false

lazy val versions = new {
  val finatra = "2.1.4"
  val guice = "4.0"
  val logback = "1.0.13"
}

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  "Twitter Maven" at "https://maven.twttr.com"
)

assemblyMergeStrategy in assembly := {
  case "BUILD" => MergeStrategy.discard
  case other => MergeStrategy.defaultMergeStrategy(other)
}

libraryDependencies ++= Seq(
  "com.twitter.finatra" %% "finatra-http" % versions.finatra,
  "com.twitter.finatra" %% "finatra-httpclient" % versions.finatra,
  "com.twitter.finatra" %% "finatra-slf4j" % versions.finatra,
  "com.twitter.inject" %% "inject-core" % versions.finatra,
  "ch.qos.logback" % "logback-classic" % versions.logback,

  "com.twitter.finatra" %% "finatra-http" % versions.finatra % "test",
  "com.twitter.finatra" %% "finatra-jackson" % versions.finatra % "test",
  "com.twitter.inject" %% "inject-server" % versions.finatra % "test",
  "com.twitter.inject" %% "inject-app" % versions.finatra % "test",
  "com.twitter.inject" %% "inject-core" % versions.finatra % "test",
  "com.twitter.inject" %% "inject-modules" % versions.finatra % "test",
  "com.google.inject.extensions" % "guice-testlib" % versions.guice % "test",

  "com.twitter.finatra" %% "finatra-http" % versions.finatra % "test" classifier "tests",
  "com.twitter.finatra" %% "finatra-jackson" % versions.finatra % "test" classifier "tests",
  "com.twitter.inject" %% "inject-app" % versions.finatra % "test" classifier "tests",
  "com.twitter.inject" %% "inject-core" % versions.finatra % "test" classifier "tests",
  "com.twitter.inject" %% "inject-modules" % versions.finatra % "test" classifier "tests",
  "com.twitter.inject" %% "inject-server" % versions.finatra % "test" classifier "tests",

  "org.mockito" % "mockito-core" % "1.9.5" % "test",
  "org.scalatest" %% "scalatest" % "2.2.3" % "test",
  "org.specs2" %% "specs2" % "2.3.12" % "test",

  "net.ruippeixotog" % "scala-scraper_2.11" % "1.0.0",
  "joda-time" % "joda-time" % "2.9.3",
  "org.scalaj" % "scalaj-http_2.11" % "2.3.0",
  "com.google.apis" % "google-api-services-youtube" % "v3-rev171-1.21.0",
  "postgresql" % "postgresql" % "9.1-901-1.jdbc4",
  "org.jooq" % "jooq" % "3.8.1",
  "org.jooq" % "jooq-meta" % "3.8.1",
  "org.jooq" % "jooq-scala" % "3.8.1",
  "org.jooq" % "jooq-codegen" % "3.8.1"
)

val generateJOOQ = taskKey[Seq[File]]("Generate JooQ classes")

val generateJOOQTask = (sourceManaged, fullClasspath in Compile, runner in Compile, streams) map { (src, cp, r, s) =>
  toError(r.run("org.jooq.util.GenerationTool", cp.files, Array("conf/db.xml"), s.log))
  ((src / "main/com/agoraphilia/model/gen") ** "*.scala").get
}

generateJOOQ <<= generateJOOQTask


unmanagedSourceDirectories in Compile += sourceManaged.value / "main/com/agoraphilia/model/gen"
