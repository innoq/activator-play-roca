import sbt._
import sbt.Keys._

object Build {
  def rocaProject(id: String): Project = Project(id, file(id))
    .settings(
      name := s"activator-play-roca-$id",
      organization := "com.innoq",
      organizationName := "innoQ Deutschland GmbH",
      scalaVersion := "2.11.1",
      scalacOptions ++= Seq("-feature", "-deprecation"),
      resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases"
    )
}