import sbt.*

object Dependencies {
  object Version {
    val zio = "2.1.13"
  }

  val zio = "dev.zio" %% "zio" % Version.zio
}
