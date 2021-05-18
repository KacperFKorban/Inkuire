import java.io.FileInputStream
import java.util.Properties

ThisBuild / name := "inkuire"
ThisBuild / organization := "org.virtuslab.inkuire"
ThisBuild / version := "0.1.2-SNAPSHOT"

ThisBuild / bintrayRepository := "Inkuire"
ThisBuild / bintrayOrganization := Some("virtuslab")

ThisBuild / homepage := Some(url("https://github.com/VirtusLab/Inkuire"))
ThisBuild / licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0"))
ThisBuild / developers := List(
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
    "fzybala@virtuslab.com",
    url("https://twitter.com/pikinier20")
  )
)

publish / skip := true
ThisBuild / scalaVersion := "2.13.4"

val http4sVersion = "0.21.0"
val catsVersion = "2.2.0"
val circeVersion = "0.13.0"

lazy val engineCommon = crossProject(JVMPlatform)
  .in(file("engineCommon"))
  .settings(
    name := "inkuire-engine-common",
    libraryDependencies ++= Seq(
      "com.softwaremill.quicklens" %%% "quicklens" % "1.6.1",
      "org.scala-lang.modules" %%% "scala-parser-combinators" % "1.1.2",
      "com.softwaremill.diffx" %%% "diffx-scalatest" % "0.3.29" % Test,
      "org.typelevel" %%% "cats-core" % catsVersion,
      "org.typelevel" %%% "cats-effect" % catsVersion,
      "org.typelevel" %%% "cats-mtl-core" % "0.7.1",
      "io.circe" %%% "circe-core" % circeVersion,
      "io.circe" %%% "circe-parser" % circeVersion,
      "io.circe" %%% "circe-generic" % circeVersion
    ),
  )

lazy val engineHttp = project
  .in(file("engineHttp"))
  .settings(
    name := "inkuire-engine-http",
    libraryDependencies ++= Seq(
      "com.softwaremill.quicklens" %% "quicklens" % "1.5.0",
      "com.softwaremill.diffx" %% "diffx-core" % "0.3.28",
      "org.scala-lang.modules" %% "scala-xml" % "1.3.0",
      "com.vladsch.flexmark" % "flexmark-all" % "0.35.10",
      "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2",
      "com.github.pureconfig" %% "pureconfig" % "0.14.0",
      "org.http4s" %% "http4s-dsl" % http4sVersion,
      "org.http4s" %% "http4s-server" % http4sVersion,
      "org.http4s" %% "http4s-client" % http4sVersion,
      "org.http4s" %% "http4s-circe" % http4sVersion,
      "org.http4s" %% "http4s-blaze-server" % http4sVersion,
      "org.slf4j" % "slf4j-simple" % "1.7.30",
      "com.lihaoyi" %% "scalatags" % "0.9.1",
      "org.typelevel" %%% "cats-core" % catsVersion,
      "org.typelevel" %%% "cats-effect" % catsVersion,
      "org.typelevel" %%% "cats-mtl-core" % "0.7.1",
      "io.circe" %%% "circe-core" % circeVersion,
      "io.circe" %%% "circe-parser" % circeVersion,
      "io.circe" %%% "circe-generic" % circeVersion
    ),
    //Test
    libraryDependencies ++= Seq(
      "junit" % "junit" % "4.12" % Test,
      "org.scalatest" %% "scalatest" % "3.2.2" % Test,
      "com.softwaremill.diffx" %% "diffx-scalatest" % "0.3.28" % Test
    ),
    assembly / mainClass := Some("org.virtuslab.inkuire.engine.http.Main")
  )
  .dependsOn(engineCommon.jvm)
