import play.PlayScala
import sbt._
import Keys._
import play.Project._

name := """activator-play-roca"""

version := "1.0-SNAPSHOT"


libraryDependencies ++= Seq(
  "org.webjars" %% "webjars-play" % "2.3-M1",
  "org.webjars" % "bootstrap" % "2.3.1",
  jdbc,
  anorm
)

lazy val root = (project in file(".")).addPlugins(PlayScala)
