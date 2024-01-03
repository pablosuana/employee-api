import com.typesafe.sbt.packager.docker.*

enablePlugins(JavaAppPackaging)

ThisBuild / version := "1.0.0"

ThisBuild / scalaVersion := "2.13.12"

val logsDeps = Seq(
  "ch.qos.logback" % "logback-classic" % "1.4.2"
)

val excludeLogs = Seq(
  ExclusionRule("org.slf4j", "slf4j-nop"),
  ExclusionRule("org.slf4j", "log4j-over-slf4j"),
  ExclusionRule("org.slf4j", "slf4j-log4j12"),
  ExclusionRule("org.slf4j", "jul-to-slf4j"),
  ExclusionRule("org.slf4j", "jcl-over-slf4j"),
  ExclusionRule("ch.qos.logback", "logback-classic"),
  ExclusionRule("org.apache.logging.log4j", "log4j-to-slf4j")
)

val serviceDeps = Seq(
  "com.typesafe.akka"             %% "akka-http"                % "10.2.7",
  "com.softwaremill.sttp.tapir"   %% "tapir-core"               % "1.2.8",
  "com.softwaremill.sttp.tapir"   %% "tapir-akka-http-server"   % "1.2.8",
  "com.softwaremill.sttp.tapir"   %% "tapir-json-circe"         % "1.2.8",
  "com.softwaremill.sttp.tapir"   %% "tapir-json-spray"         % "1.2.8",
  "com.softwaremill.sttp.tapir"   %% "tapir-prometheus-metrics" % "1.2.8",
  "com.softwaremill.sttp.tapir"   %% "tapir-openapi-docs"       % "1.2.8",
  "com.softwaremill.sttp.apispec" %% "openapi-model"            % "0.3.2",
  "com.softwaremill.sttp.apispec" %% "openapi-circe"            % "0.3.2",
  "com.softwaremill.sttp.apispec" %% "openapi-circe-yaml"       % "0.3.2",
  "com.h2database"                % "h2"                        % "2.2.220",
  "com.zaxxer"                    % "HikariCP"                  % "5.0.1",
  "com.typesafe"                  % "config"                    % "1.4.2"
).map(_.excludeAll(excludeLogs: _*))

val testDeps = Seq(
  "org.scalatest"  %% "scalatest" % "3.2.15",
  "com.h2database" % "h2"         % "2.2.220",
  "com.zaxxer"     % "HikariCP"   % "5.0.1",
  "com.typesafe"   % "config"     % "1.4.2"
).map(_ % Test)

lazy val root = (project in file("."))
  .settings(
    name := "employee-api",
    fork := false
  )
  .settings(
    libraryDependencies ++= (testDeps ++ serviceDeps ++ logsDeps)
  )
  .settings(
    dockerCommands ++= Seq(
      Cmd("USER", "root"),
      Cmd("RUN", "dir"),
      Cmd("COPY", "/opt/docker/schema.sql", "/tmp/schema.sql"),
      Cmd("COPY", "/opt/docker/data.sql", "/tmp/data.sql")
    )
  )
  .settings(Universal / mappings := (Universal / mappings).value :+ (file(s"${baseDirectory.value}/src/main/resources/schema.sql") -> "schema.sql") :+ (file(s"${baseDirectory.value}/src/main/resources/data.sql") -> "data.sql"))

  .enablePlugins(DockerPlugin, JavaServerAppPackaging)
