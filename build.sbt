import BuildHelper.*

lazy val root = (project in file("."))
  .settings(nameSettings)
  .settings(standardSettings)
  .settings(
    libraryDependencies ++=
      List(
        Dependencies.zio
      )
  )
