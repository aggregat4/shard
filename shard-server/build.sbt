name := "shard-server"

version := "1.0"

scalaVersion := "2.9.1"

// Add Twitter's Repository
resolvers += "twitter.com" at "http://maven.twttr.com/"

resolvers += "Typesafe repo" at "http://repo.typesafe.com/typesafe/releases/"
   
resolvers += "spray repo" at "http://repo.spray.cc/"

// ---- COMPILE
// twitter suffixes artifact ids with "_2.9.1" for the 2.9 compatible version

libraryDependencies += "com.twitter" % "finagle-core_2.9.1" % "3.0.0"

libraryDependencies += "com.twitter" % "finagle-http_2.9.1" % "3.0.0"



libraryDependencies += "se.scalablesolutions.akka" %  "akka-actor" % "1.3.1" % "compile"

libraryDependencies += "cc.spray" % "spray-can" % "0.9.3" % "compile"

libraryDependencies += "cc.spray" % "spray-server" % "0.9.0" % "compile"

libraryDependencies += "se.scalablesolutions.akka" % "akka-slf4j" % "1.3.1"

libraryDependencies += "org.slf4j" % "slf4j-api" % "1.6.4"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.0.0"



libraryDependencies += "org.fusesource.scalate" % "scalate-core" % "1.5.3"


// ---- TEST

//libraryDependencies += "org.specs2" %% "specs2" % "1.7.1"  % "test"

//libraryDependencies += "org.scalatest" %% "scalatest" % "1.7.1" % "test"

libraryDependencies += "junit" % "junit" % "4.10" % "test"

libraryDependencies += "org.easytesting" % "fest-assert" % "1.4" % "test"