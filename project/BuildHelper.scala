import sbt.*
import sbt.Keys.*

object BuildHelper {
  val scala3 = "3.3.1"

  def nameSettings: List[Setting[String]] = List(
    name             := "zionomicon-exercises",
    organization     := "io.kevchuang",
    organizationName := "kevchuang"
  )

  def standardSettings: List[
    Setting[? >: String & Task[Seq[String]] & Boolean & Seq[TestFramework]]
  ] = List(
    ThisBuild / scalaVersion := scala3,
    scalacOptions            := ScalaSettings.baseSettings,
    Test / parallelExecution := true,
    ThisBuild / fork         := true,
    run / fork               := true
  )
}
