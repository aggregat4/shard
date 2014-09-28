name := "shard"

version := "1.0"

scalaVersion := "2.10.0"

resolvers += "Typesafe repo" at "http://repo.typesafe.com/typesafe/releases/"
   
// ---- COMPILE
libraryDependencies += "org.slf4j" % "slf4j-api" % "1.6.4"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.0.0"

libraryDependencies += "org.eclipse.jetty" % "jetty-server" % "9.0.0.M4"

libraryDependencies += "org.eclipse.jetty" % "jetty-servlet" % "9.0.0.M4"

libraryDependencies += "javax.servlet" % "javax.servlet-api" % "3.0.1"

libraryDependencies += "com.github.spullara.mustache.java" % "compiler" % "0.8.9"

libraryDependencies += "com.typesafe" % "config" % "1.0.0"

libraryDependencies += "eu.henkelmann" % "actuarius_2.10.0" % "0.2.6"

libraryDependencies += "commons-io" % "commons-io" % "2.4"

libraryDependencies += "com.google.guava" % "guava" % "14.0"

libraryDependencies += "com.jsuereth" % "scala-arm_2.10" % "1.3"

libraryDependencies += "com.scalatags" % "scalatags" % "0.4.1"

// ---- TEST
libraryDependencies += "junit" % "junit" % "4.10" % "test"

libraryDependencies += "org.easytesting" % "fest-assert" % "1.4" % "test"

libraryDependencies += "com.novocode" % "junit-interface" % "0.8" % "test->default"

// this is a workaround for the jetty dependencies not properly resolving (see http://stackoverflow.com/questions/9889674/sbt-jetty-and-servlet-3-0 )
ivyXML := <dependency org="org.eclipse.jetty.orbit" name="javax.servlet" rev="3.0.0.v201112011016"><artifact name="javax.servlet" type="orbit" ext="jar"/></dependency>

// needed to add resource directories to the classpath https://github.com/typesafehub/sbteclipse/issues/73
//EclipseKeys.createSrc := EclipseCreateSrc.Default + EclipseCreateSrc.Resource


//resolvers += "twitter.com" at "http://maven.twttr.com/"
//resolvers += "spray repo" at "http://repo.spray.cc/"
//libraryDependencies += "org.fusesource.scalate" % "scalate-core" % "1.5.3"

