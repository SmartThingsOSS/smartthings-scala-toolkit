
lazy val compileScalastyle = taskKey[Unit]("compileScalastyle")

lazy val common = Seq(
  organization := "com.smartthings.scalakit",
  git.baseVersion := "1.0",
  scalaVersion := "2.13.2",

  crossScalaVersions := Seq("2.12.10", "2.13.2"),

  compileScalastyle := scalastyle.in(Compile).toTask("").value,

  scalacOptions ++= Seq(
    "-deprecation"
  ),
  
  javacOptions ++= Seq("-source", "8", "-target", "8", "-Xlint"),

  libraryDependencies ++= Seq(
    // Testing
    "org.scalatest" %% "scalatest"                  % "3.1.1" % Test,
  ),

  // run style and header checks on test task
  Test / test := (((Test / test) dependsOn compileScalastyle) dependsOn Compile / headerCheck).value,
  
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
    organizationName := "SmartThings",
    startYear := Some(2018),
    licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html")),
    pomIncludeRepository := (_ => false),
    publishMavenStyle := true,
    publishTo := Def.taskDyn {
      val original = publishTo.value
      Def.task {
        if (version.value.endsWith("-SNAPSHOT"))
          Some("Artifactory Realm" at "https://oss.jfrog.org/artifactory/oss-snapshot-local")
        else
          original
      }
    }.value,
    bintrayReleaseOnPublish := (if (version.value.endsWith("-SNAPSHOT")) false else true),
    bintrayOrganization := Some("smartthingsoss"),
    bintrayPackage := "smartthings-scala-toolkit",
    credentials := List(
      Path.userHome / ".bintray" / ".artifactory")
        .filter(_.exists())
        .map(Credentials(_))
  )

lazy val root = project.in(file("."))
  .enablePlugins(GitBranchPrompt)
  .settings(settings)
  .settings(
    name := "scala-toolkit-root",
    publishArtifact := false,
    Compile / unmanagedSourceDirectories := Seq.empty,
    Test / unmanagedSourceDirectories    := Seq.empty,
  )
  .aggregate(core, modules, config, monix)

lazy val modules = project.in(file("modules"))
  .dependsOn(core)
  .settings(settings)
  .settings(
    name := "scala-toolkit-modules",
    
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-effect"                % "2.1.3",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
    ),
  )

lazy val core = project.in(file("core"))
  .settings(settings)
  .settings(
    name := "scala-toolkit-core",

    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-effect"                % "2.1.3",
    )
  )

lazy val config = project.in(file("config"))
  .settings(settings)
  .settings(
    name := "scala-toolkit-config",

    libraryDependencies ++= Seq(
      "com.github.pureconfig" %% "pureconfig"         % "0.12.3",
      "com.github.scopt" %% "scopt"                   % "3.7.1",
    ),
  )

lazy val monix = project.in(file("monix"))
  .settings(settings)
  .settings(
    name := "scala-toolkit-monix",

    libraryDependencies ++= Seq(
      "io.monix" %% "monix" % "3.1.0",
    )
  )
