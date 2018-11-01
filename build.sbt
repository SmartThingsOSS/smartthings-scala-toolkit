
lazy val common = Seq(
  organization := "com.smartthings.scalakit",
  git.baseVersion := "1.0",
  scalaVersion := "2.12.7",

  scalacOptions ++= Seq(
    "-deprecation",
    "-Ypartial-unification"
  ),
  
  javacOptions ++= Seq("-source", "8", "-target", "8", "-Xlint"),

  libraryDependencies ++= Seq(
    // Testing
    "org.scalatest" %% "scalatest"                  % "3.0.5" % Test,
  ),
  
  initialize := {
    if (sys.props("java.specification.version").toDouble < 1.8)
      sys.error("Java 1.8 or newer is required for this project.")
  },
)

lazy val root = project.in(file("."))
  .enablePlugins(GitBranchPrompt)
  .settings(common)
  .settings(
    name := "scala-toolkit-root"
  )
  .aggregate(core, modules, config)

lazy val modules = project.in(file("modules"))
  .dependsOn(core)
  .settings(common)
  .settings(
    name := "scala-toolkit-modules",
    
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-effect"                % "1.1.0",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0",
    ),
  )

lazy val core = project.in(file("core"))
  .settings(common)
  .settings(
    name := "scala-toolkit-core",

    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-effect"                % "1.0.0",
    )
  )

lazy val config = project.in(file("config"))
  .settings(common)
  .settings(
    name := "scala-toolkit-config",

    libraryDependencies ++= Seq(
      "com.github.pureconfig" %% "pureconfig"         % "0.10.1",
      "com.github.scopt" %% "scopt"                   % "3.7.0",
    ),
  )
