import sbt.Keys._

val scalaV = "2.11.7"
val scalazV = "7.1.3"

scalacOptions += "-feature"

scalaVersion := scalaV

resolvers ++= Seq(
    Resolver.mavenLocal,
    DefaultMavenRepository,
    "jcenter" at "http://jcenter.bintray.com",
    "47 Degrees Bintray Repo" at "http://dl.bintray.com/47deg/maven",
    Resolver.typesafeRepo("releases"),
    Resolver.typesafeRepo("snapshots"),
    Resolver.typesafeIvyRepo("snapshots"),
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots"),
    Resolver.defaultLocal,
    "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"
  ) 

libraryDependencies ++= Seq(
  "org.scalaz" %% "scalaz-core" % scalazV,
  "org.scalaz" %% "scalaz-effect" % scalazV,
  "org.scalaz" %% "scalaz-typelevel" % scalazV,
  "org.scalaz" %% "scalaz-scalacheck-binding" % scalazV % "test",
  "com.chuusai" %% "shapeless" % "2.2.5",
  "org.scala-lang" % "scala-compiler" % scalaV,
  "com.propensive" %% "rapture-core" % "2.0.+" changing(),
  "com.propensive" %% "rapture-core-scalaz" % "2.0.+" changing()	
)

scalacOptions in (Compile, console) ++= Seq(
  "-i", "myrepl.init"
)