name := """Gadgets"""

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "org.webjars" % "angularjs" % "1.3.0-beta.2",
  "org.webjars" % "requirejs" % "2.1.11-1",
  "com.typesafe.slick" %% "slick" % "3.0.1",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "com.zaxxer"           %      "HikariCP-java6"          %      "2.3.9",
  "org.scalatest"        %%    "scalatest"    	      %      "2.2.5"     %    "test",
  "mysql" %  "mysql-connector-java"  %   "5.1.36"
)     

lazy val root = (project in file(".")).enablePlugins(PlayScala)

pipelineStages := Seq(rjs, digest, gzip)

EclipseKeys.createSrc := EclipseCreateSrc.All