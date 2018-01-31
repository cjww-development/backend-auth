import com.typesafe.config.ConfigFactory
import sbt.Keys.scalaVersion

import scala.util.{Failure, Success, Try}

val btVersion: String = Try(ConfigFactory.load.getString("version")) match {
  case Success(ver) => ver
  case Failure(_)   => "0.1.0"
}

val libraryName = "backend-auth"

val configKeyBase = "microservice.data-security"

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
    resolvers                            +=  "cjww-dev" at "http://dl.bintray.com/cjww-development/releases",
    fork                    in Test      :=  true,
    javaOptions             in Test      :=  Seq(
      "-Dmicroservice.data-security.key=testKey",
      "-Dmicroservice.data-security.salt=testSalt",
      "-Dmicroservice.external-services.deversity-frontend.application-id=testDevFEId",
      "-Dmicroservice.external-services.deversity.application-id=testDevId",
      "-Dmicroservice.external-services.diagnostics-frontend.application-id=testDiagFEId",
      "-Dmicroservice.external-services.hub-frontend.application-id=testHubFEId",
      "-Dmicroservice.external-services.auth-service.application-id=testAuthFEId",
      "-Dmicroservice.external-services.auth-microservice.application-id=testAuthBeId",
      "-Dmicroservice.external-services.accounts-microservice.application-id=testAccBeId",
      "-Dmicroservice.external-services.session-store.application-id=testSessionStoreId"
    )
  )
