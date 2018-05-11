
import org.apache.spark._
import org.apache.spark.streaming._
import org.apache.spark.streaming.twitter._
import org.apache.spark.streaming.twitter.TwitterUtils

import twitter4j.TwitterFactory
import twitter4j.Twitter
import twitter4j.auth
import twitter4j.auth._
import twitter4j.conf.ConfigurationBuilder

import org.apache.spark.storage.StorageLevel
import org.apache.log4j.{Level, Logger}


import edu.stanford.nlp.pipeline.CoreNLPProtos.Sentiment
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations
import edu.stanford.nlp.ling.{CoreAnnotations, CoreLabel}
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations
import edu.stanford.nlp.pipeline.{CleanXmlAnnotator, StanfordCoreNLP}
import java.util.Properties

import scala.collection.JavaConverters._




object sparktweet {


/* sentiment analysis functions - com.databricks.spark.corenlp.functions */ 

@transient private var sentimentPipeline: StanfordCoreNLP = _

private def getOrCreateSentimentPipeline(): StanfordCoreNLP = {
    if (sentimentPipeline == null) {
      val props = new Properties()
      props.setProperty("annotators", "tokenize, ssplit, parse, sentiment")
      sentimentPipeline = new StanfordCoreNLP(props)
    }
    sentimentPipeline
  }


 def sentiment =  { sentence: String =>
    val pipeline = getOrCreateSentimentPipeline()
    val annotation = pipeline.process(sentence)
    val tree = annotation.get(classOf[CoreAnnotations.SentencesAnnotation])
      .asScala
      .head
      .get(classOf[SentimentCoreAnnotations.SentimentAnnotatedTree])
    RNNCoreAnnotations.getPredictedClass(tree)
  }

/************************/



/* remove hashtags from tweets */
def removehashtags = { status :	String =>
			val hashregex = "#(\\S)*".r
			val snoht= hashregex.replaceAllIn(status,"")
			snoht
			} 

/* remove mentions from tweets */
def removementions = { status : String =>
		       val mentionregex = "@(\\S)*".r
		       val snom = mentionregex.replaceAllIn(status,"")	
		       snom
}


/* remove URLs from tweets */
def removeurls = { status :String => 
		   val urlregex = "(https://)(\\S)*".r
		   val snourl = urlregex.replaceAllIn(status,"")
		   snourl
		
}


/* remove retweet flag RT */
def removert = { status : String =>
		 val rtregex = "RT".r
		 val snort = rtregex.replaceAllIn(status,"")
		 snort
		}


/* remove special characters */
def removesc = {status : String =>
		val scregex = "[^a-zA-Z0-9,.!'\n\t ]".r
		val snosc = scregex.replaceAllIn(status,"")
		snosc		
}

/* remove white spaces */
def removews = { status : String =>
		     val wsregex= "[\n\t]".r
		     val snows = wsregex.replaceAllIn(status," ")
		     snows
}

def clean = { status : String =>
		val ht = removehashtags(status)
		val ment = removementions(ht)
		val url = removeurls(ment)
		val rt = removert(url)
		val sc= removesc(rt)
		val ws =removews(sc)
		ws
	     }


def main(args: Array[String]) {

// streaming context 
val conf = new SparkConf().setAppName("Twitter_Sentiment")
val sc = new SparkContext(conf)
val ssc = new StreamingContext(sc, Seconds(1))


/* set twitter authentication using system properties */
/*
System.setProperty("twitter4j.oauth.consumerKey", "***************")
System.setProperty("twitter4j.oauth.consumerSecret", "***************")
System.setProperty("twitter4j.oauth.accessToken", "***************")
System.setProperty("twitter4j.oauth.accessTokenSecret", "***************")
*/



/* set Twitter authentication using configuration builder */

val cb = new ConfigurationBuilder()
cb.setDebugEnabled(false).setOAuthConsumerKey("***************").setOAuthConsumerSecret("***************").setOAuthAccessToken("***************").setOAuthAccessTokenSecret("***************")
cb.setTweetModeExtended(true).setIncludeEntitiesEnabled(false)
val auth = new OAuthAuthorization(cb.build)




/* filtering words */
val filter = Seq("cloudera","cldr")

/* set up the twitter stream */
val stream = TwitterUtils.createStream( ssc , Some(auth) , filter )


/****** Sentiment Test ******/

/** truncated cleaned tweets **/

/*  val cleand = stream.foreachRDD{ rdd => rdd.map(x => clean(x.getText())).foreach{x => println(x)
                                                                                     println(sentiment(x))
                                                                                     println("****end of tweet*****")}
                                }
*/

/** non-truncated cleaned tweets **/

 val cleaned = stream.foreachRDD{ rdd => rdd.map(x => clean(x.getRetweetedStatus().getText())).foreach{ x => println(x)
													    println(sentiment(x))
												            println("****end of tweet*****")} 

				} 

ssc.start()
ssc.awaitTermination()

}

}
