import com.typesafe.config.ConfigFactory
import scala.util.{Try, Success, Failure}

val btVersion: String = Try(ConfigFactory.load.getString("version")) match {
  case Success(ver) => ver
  case Failure(_) => "0.1.0"
}

name := "backend-auth"
version := btVersion
scalaVersion := "2.11.11"
organization := "com.cjww-dev.libs"

val cjwwDep: Seq[ModuleID] = Seq(
  "com.cjww-dev.libs" %% "http-verbs" % "1.0.0",
  "com.cjww-dev.libs" %% "logging" % "0.6.0",
  "com.cjww-dev.libs" %% "bootstrapper" % "1.5.0",
  "com.cjww-dev.libs" %% "application-utilities" % "0.2.0"
)
val codeDep: Seq[ModuleID] = Seq("com.typesafe.play" % "play_2.11" % "2.5.15")
val testDep: Seq[ModuleID] = Seq(
  "org.scalatestplus.play" % "scalatestplus-play_2.11" % "2.0.0",
  "org.mockito" % "mockito-core" % "2.8.9"
)

libraryDependencies ++= cjwwDep
libraryDependencies ++= codeDep
libraryDependencies ++= testDep

resolvers += "cjww-dev" at "http://dl.bintray.com/cjww-development/releases"

bintrayOrganization := Some("cjww-development")
bintrayReleaseOnPublish in ThisBuild := false
bintrayRepository := "releases"
bintrayOmitLicense := true
