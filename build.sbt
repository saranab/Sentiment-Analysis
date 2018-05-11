
name := "sparktweet"

version := "1.0"

scalaVersion := "2.11.5"







 


libraryDependencies ++=Seq( "org.apache.spark" %% "spark-sql" % "2.0.1",
			    "org.apache.bahir" % "spark-streaming-twitter_2.11" % "2.0.1",
			    "org.apache.spark" % "spark-streaming_2.11" % "2.0.1",
			    "org.twitter4j" % "twitter4j-stream" % "4.0.6",
			    "edu.stanford.nlp" % "stanford-corenlp" % "3.6.0",
			    "com.google.protobuf" % "protobuf-java" % "2.6.1",
		            "edu.stanford.nlp" % "stanford-corenlp" % "3.6.0" classifier "models",
		            "org.scalatest" %% "scalatest" % "2.2.6" % "test")
 
assemblyMergeStrategy in assembly := {

 case PathList("META-INF", xs @ _*) => MergeStrategy.discard
 case x => MergeStrategy.first
 
 case PathList("org", "apache", "spark", "unused", "UnusedStubClass.class") =>
    MergeStrategy.first
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}


resolvers += Resolver.bintrayIvyRepo("com.eed3si9n", "sbt-plugins")
