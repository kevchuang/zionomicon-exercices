import sbt.*

object Dependencies {
  object Version {
    val zio = "2.1.13"
  }

  val zio            = "dev.zio" %% "zio"          % Version.zio
  val `zio-test`     = "dev.zio" %% "zio-test"     % Version.zio
  val `zio-test-sbt` = "dev.zio" %% "zio-test-sbt" % Version.zio
}
