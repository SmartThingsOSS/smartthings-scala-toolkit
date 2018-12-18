
lazy val common = Seq(
  organization := "com.smartthings.scalakit",
  git.baseVersion := "1.0",
  scalaVersion := "2.12.7",

  scalacOptions ++= Seq(
    "-deprecation",
    "-Ypartial-unification"
  ),
  
  javacOptions ++= Seq("-source", "8", "-target", "8", "-Xlint"),

  licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html")),

  libraryDependencies ++= Seq(
    // Testing
    "org.scalatest" %% "scalatest"                  % "3.0.5" % Test,
  ),
  
  initialize := {
    if (sys.props("java.specification.version").toDouble < 1.8)
      sys.error("Java 1.8 or newer is required for this project.")
  },
)

lazy val settings = common ++ publishSettings

lazy val publishSettings =
  Seq(
    homepage := Some(url("https://github.com/smartthingsoss/smartthings-scala-toolkit")),
    scmInfo := Some(ScmInfo(url("https://github.com/smartthingsoss/smartthings-scala-toolkit"),
      "git@github.com:smartthingsoss/smartthings-scala-toolkit.git")),
    developers += Developer("llinder",
      "Lance Linder",
      "lance@smartthings.com",
      url("https://github.com/llinder")),
    pomIncludeRepository := (_ => false),
    publishMavenStyle := true,
    bintrayOrganization := Some("smartthingsoss"),
    bintrayPackage := "smartthings-scala-toolkit"
  )

lazy val root = project.in(file("."))
  .enablePlugins(GitBranchPrompt)
  .settings(settings)
  .settings(
    name := "scala-toolkit-root",
    publishArtifact := false,
    bintrayRelease := false,
    Compile / unmanagedSourceDirectories := Seq.empty,
    Test / unmanagedSourceDirectories    := Seq.empty,
  )
  .aggregate(core, modules, config)

lazy val modules = project.in(file("modules"))
  .dependsOn(core)
  .settings(settings)
  .settings(
    name := "scala-toolkit-modules",
    
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-effect"                % "1.1.0",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0",
    ),
  )

lazy val core = project.in(file("core"))
  .settings(settings)
  .settings(
    name := "scala-toolkit-core",

    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-effect"                % "1.0.0",
    )
  )

lazy val config = project.in(file("config"))
  .settings(settings)
  .settings(
    name := "scala-toolkit-config",

    libraryDependencies ++= Seq(
      "com.github.pureconfig" %% "pureconfig"         % "0.10.1",
      "com.github.scopt" %% "scopt"                   % "3.7.0",
    ),
  )
