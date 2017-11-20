import com.typesafe.config.ConfigFactory
import sbt.Keys.scalaVersion

import scala.util.{Failure, Success, Try}

val btVersion: String = Try(ConfigFactory.load.getString("version")) match {
  case Success(ver) => ver
  case Failure(_)   => "0.1.0"
}

val libraryName = "backend-auth"

lazy val library = Project(libraryName, file("."))
  .settings(
      version                              :=  btVersion,
      scalaVersion                         :=  "2.11.11",
      organization                         :=  "com.cjww-dev.libs",
      libraryDependencies                  ++= LibraryDependencies(),
      bintrayOrganization                  :=  Some("cjww-development"),
      bintrayReleaseOnPublish in ThisBuild :=  true,
      bintrayRepository                    :=  "releases",
      bintrayOmitLicense                   :=  true,
      resolvers                            +=  "cjww-dev" at "http://dl.bintray.com/cjww-development/releases"
  )




