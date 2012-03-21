name := "shard-server"

version := "1.0"

scalaVersion := "2.9.1"

// Add Twitter's Repository
resolvers += "twitter.com" at "http://maven.twttr.com/"

//resolvers += "FuseSource Snapshot Repository" at "http://repo.fusesource.com/nexus/content/repositories/snapshots"

// twitter suffixes artifact ids with "_2.9.1" for the 2.9 compatible version
libraryDependencies += "com.twitter" % "finagle-core_2.9.1" % "3.0.0"

libraryDependencies += "com.twitter" % "finagle-http_2.9.1" % "3.0.0"

libraryDependencies += "org.fusesource.scalate" % "scalate-core" % "1.5.3"

libraryDependencies += "org.scalatest" %% "scalatest" % "1.7.1" % "test"
