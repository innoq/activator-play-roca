import sbt._

object Dependencies {
  val playVersion = "2.3.0"

  object Organizations {
    val webJars = "org.webjars"
  }

  val jodaTime = "joda-time" % "joda-time" % "2.3"
  val jodaConvert = "org.joda" % "joda-convert" % "1.6"

  val halselhof = uri("git://github.com/tobnee/HALselhof.git")

  val webJarsPlay = Organizations.webJars %% "webjars-play" % playVersion
  val bootstrap = Organizations.webJars % "bootstrap" % "3.1.1-1"


}