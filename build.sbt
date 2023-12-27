ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"

val testDeps = Seq(
  "org.scalatest"  %% "scalatest" % "3.2.15",
  "com.h2database" % "h2"         % "2.2.220",
  "com.zaxxer"     % "HikariCP"   % "5.0.1",
  "com.typesafe"   % "config"     % "1.4.2"
).map(_ % Test)

lazy val root = (project in file("."))
  .settings(
    name := "employee-api"
  )
  .settings(
    libraryDependencies ++= testDeps
  )
