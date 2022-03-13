
publish / skip := true

val scala213 = "2.13.8"
val scala3 = "3.1.1"

val orgSettings = Seq(
  organization := "org.virtuslab",
  homepage := Some(url("https://github.com/VirtusLab/Inkuire")),
  licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
  developers := List(
    Developer(
      "KacperFKorban",
      "Kacper Korban",
      "kacper.f.korban@gmail.com",
      url("https://twitter.com/KacperKorban")
    ),
    Developer(
      "BarkingBad",
      "Andrzej Ratajczak",
      "andrzej.ratajczak98@gmail.com",
      url("https://twitter.com/aj_ratajczak")
    ),
    Developer(
      "pikinier20",
      "Filip Zybała",
      "filip.zybala@gmail.com",
      url("https://twitter.com/pikinier20")
    )
  ),
)

val scalafixSettings = Seq(
//   semanticdbEnabled := true,
//   semanticdbVersion := scalafixSemanticdb.revision,
)
// ThisBuild / scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.5.0"
// ThisBuild / scalafixScalaBinaryVersion := scalaVersion.value

val commonSettings = orgSettings ++ scalafixSettings ++ Seq(
  scalaVersion := scala3,
  scalacOptions ++= Seq(
    "-Yrangepos",
    "-Ywarn-unused",
    "-deprecation",
    "-feature"
  ),
)

val http4sVersion = "0.23.10"
val circeVersion = "0.14.1"
val monixVersion = "3.4.0"

lazy val inkuireEngine = projectMatrix
  .in(file("engine"))
  .settings(commonSettings)
  .settings(
    name := "inkuire-engine",
    libraryDependencies ++= Seq(
      "com.softwaremill.quicklens" %%% "quicklens" % "1.8.4",
      "org.scala-lang.modules" %%% "scala-parser-combinators" % "2.1.1",
      "io.circe" %%% "circe-core" % circeVersion,
      "io.circe" %%% "circe-parser" % circeVersion,
      "io.circe" %%% "circe-generic" % circeVersion,
      "org.scalameta" %% "munit" % "0.7.29" % Test
    ),
  )
  .jvmPlatform(
    scalaVersions = Seq(scala3, scala213)
  )
  .jsPlatform(
    scalaVersions = Seq(scala3, scala213)
  )

lazy val inkuireHttp = projectMatrix
  .in(file("http"))
  .settings(commonSettings)
  .settings(
    name := "inkuire-http",
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-dsl" % http4sVersion,
      "org.http4s" %% "http4s-server" % http4sVersion,
      "org.http4s" %% "http4s-blaze-server" % http4sVersion,
      "org.slf4j" % "slf4j-simple" % "1.7.30",
      "com.lihaoyi" %% "scalatags" % "0.11.1"
    ),
    assembly / mainClass := Some("org.virtuslab.inkuire.http.Main")
  )
  .jvmPlatform(
    scalaVersions = Seq(scala3, scala213)
  )
  .dependsOn(inkuireEngine)

lazy val inkuireJs = projectMatrix
  .in(file("js"))
  .settings(commonSettings)
  .enablePlugins(ScalaJSPlugin)
  .settings(
    name := "inkuire-js",
    libraryDependencies ++= Seq(
      "com.softwaremill.sttp.client" %%% "core" % "2.2.9" cross CrossVersion.for3Use2_13,
      "io.monix" %%% "monix" % monixVersion,
      "io.monix" %%% "monix-reactive" % monixVersion
    ),
    scalaJSUseMainModuleInitializer := true,
  )
  .jsPlatform(
    scalaVersions = Seq(scala3, scala213)
  )
  .dependsOn(inkuireEngine)
