import play.PlayScala
import Build._
import Dependencies._

lazy val domain = rocaProject("domain")
  .settings(
    libraryDependencies ++= Seq(
      jodaTime, jodaConvert
    )
  )

lazy val infra = rocaProject("infra")
  .settings(
    libraryDependencies ++= Seq(
      anorm, jdbc
    )
  )
  .dependsOn(domain)

lazy val web = rocaProject("web").in(file("."))
  .enablePlugins(PlayScala, SbtWeb)
  .settings(
    libraryDependencies ++= Seq(
      webJarsPlay, bootstrap, jodaTime, jodaConvert, anorm, jdbc),
    pipelineStages := Seq(rjs),
    unmanagedResourceDirectories in Assets += baseDirectory.value / "vendor" / "assets" / "bower_components")
  .aggregate(domain, infra)
  .dependsOn(domain, infra, halselhof)
