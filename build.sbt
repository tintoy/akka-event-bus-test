name := "event-bus-test"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= {
  val akkaVersion = "2.4.0"
  val scalaTestVersion = "2.2.4"

  Seq(
    "com.typesafe.akka" %% "akka-actor"   % akkaVersion,
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
    "org.scalatest"     %% "scalatest"    % scalaTestVersion  % "test"
  )
}
