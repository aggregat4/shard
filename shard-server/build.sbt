name := "shard-server"

version := "1.0"

scalaVersion := "2.9.1"

//resolvers += "twitter.com" at "http://maven.twttr.com/"

resolvers += "Typesafe repo" at "http://repo.typesafe.com/typesafe/releases/"
   
resolvers += "spray repo" at "http://repo.spray.cc/"

// ---- COMPILE

libraryDependencies += "se.scalablesolutions.akka" %  "akka-actor" % "1.3.1" % "compile"

libraryDependencies += "cc.spray" % "spray-can" % "0.9.3" % "compile"

libraryDependencies += "cc.spray" % "spray-server" % "0.9.0" % "compile"



libraryDependencies += "se.scalablesolutions.akka" % "akka-slf4j" % "1.3.1"

libraryDependencies += "org.slf4j" % "slf4j-api" % "1.6.4"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.0.0"



libraryDependencies += "org.fusesource.scalate" % "scalate-core" % "1.5.3"

libraryDependencies += "com.typesafe.config" % "config" % "0.3.0"

// ---- TEST

libraryDependencies += "junit" % "junit" % "4.10" % "test"

libraryDependencies += "org.easytesting" % "fest-assert" % "1.4" % "test"

libraryDependencies += "com.novocode" % "junit-interface" % "0.8" % "test->default"
