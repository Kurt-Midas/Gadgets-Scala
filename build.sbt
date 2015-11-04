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

lazy val commonSettings = Seq(
	organization 	:= 	"org.gadgets",
	version 		:= 	"0.1-unfinished",
	scalaVersion	:=	"2.11.7",
	pipelineStages	:=	Seq(rjs, digest, gzip)
)

lazy val root = (project in file(".")).
	enablePlugins(PlayScala).
	settings(commonSettings: _*).
	aggregate(dbmanager, planetary)

//pipelineStages := Seq(rjs, digest, gzip)

EclipseKeys.createSrc := EclipseCreateSrc.All

fork in run := false

lazy val dbmanager = (project in file("dbmanager")).
	settings(commonSettings: _*)
	
lazy val planetary = (project in file("planetary")).
	enablePlugins(PlayScala).
	settings(commonSettings: _*)