import sbt.Keys.libraryDependencies

val sparkVersion = "2.3.0"
val localMavenHttps = "https://s3-us-west-2.amazonaws.com/net-mozaws-data-us-west-2-ops-mavenrepo/"
resolvers += "S3 local maven snapshots" at localMavenHttps + "snapshots"

lazy val root = (project in file(".")).
  settings(
    name := "message-jvalue",
    version := "1.0",
    scalaVersion := "2.11.8",
    libraryDependencies += "com.mozilla.telemetry" %% "moztelemetry" % "1.1-SNAPSHOT",
    libraryDependencies += "org.apache.spark" %% "spark-core" % sparkVersion,
    libraryDependencies += "org.apache.spark" %% "spark-sql" % sparkVersion,
    libraryDependencies += "org.rogach" %% "scallop" % "3.1.3",
    libraryDependencies += "org.json4s" %% "json4s-jackson" % "3.2.11",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test"
)

test in assembly := {}
assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case _ => MergeStrategy.first
}
