import sbt._

object Dependencies {

  object Organizations {
    val webJars = "org.webjars"
  }

  val jodaTime = "joda-time" % "joda-time" % "2.3"
  val jodaConvert = "org.joda" % "joda-convert" % "1.6"

  val halselhof = uri("git://github.com/tobnee/HALselhof.git")

  val webJarsPlay = Organizations.webJars %% "webjars-play" % "2.3.0"
  val bootstrap = Organizations.webJars % "bootstrap" % "3.1.1-1"

  val macwire = "com.softwaremill.macwire" %% "macros" % "0.7"


}