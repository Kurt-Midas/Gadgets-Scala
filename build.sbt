import NativePackagerKeys._

name := """Gadgets"""

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

lazy val root = Project(
	id = "root",
	base = file(".")
).aggregate(dbmanager, planetary, scrap, webapi).
	settings(commonSettings: _*).
	dependsOn(dbmanager, planetary, scrap, webapi).
	enablePlugins(PlayScala)


EclipseKeys.createSrc := EclipseCreateSrc.All

fork in run := false

lazy val dbmanager = Project(
	id = "dbmanager",
	base = file("modules/dbmanager")
).enablePlugins(PlayScala).settings(commonSettings: _*)

lazy val planetary = Project(
	id = "planetary",
	base = file("modules/planetary")
).enablePlugins(PlayScala).settings(commonSettings: _*).dependsOn(dbmanager)

lazy val scrap = Project(
	id = "scrap",
	base = file("modules/scrap")
).enablePlugins(PlayScala).settings(commonSettings: _*).dependsOn(dbmanager)

lazy val webapi = Project(
	id = "webapi",
	base = file("modules/webapi")
).enablePlugins(PlayScala).settings(commonSettings: _*)