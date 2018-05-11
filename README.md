# Sentiment-Analysis
# Twitter Sentiment Analysis using Spark Streaming

- Functionality : apply sentiment analysis on a stream of real-time Twitter Status that are related to Cloudera.  

  The output is rated to a scale between 0 and 4 where :  
  
    0: Extremely Negative  
    1: Negative  
    2: Neutral  
    3: Positive  
    4:Extremely Positive  
    

- Tools and Libraries:
  - Spark Streaming : streaming tweets from Twitter API
  - Twitter4j : connection with Twitter API, and manipulating its response.
  - Stanford CoreNLP: NLP library provides a set of human language technology tools. Used for Sentiment Analysis. https://stanfordnlp.github.io/CoreNLP/
  - Scala : Spark application is written in Scala.
  - SBT: compiling, building, and packaging the project.

- Steps followed:
    - Install java.
    - Install Scala.
    - Install Spark.
    - Install SBT.
    - Create SBT project directory:  
      
         ```sh
            - Project Directory  
                        - src
		                |    - main
		                |    |	- scala /* scala main classes */
		                |    |	|
	                    |    |  - java  /* java main classes */
		                |    - test
		                |    |	- scala /* scala test classes */
		                |    |	|
                        |    |  - java  /* java test classes*/
                        - target
                        |
                        - project 
                        |    - assembly.sbt  /* add assembly sbt plugin for deploying jar */
                        - build.sbt /* build definition */
         ```
  
    - Create spark application. 
    - Deploy jar file using:
      ```sh
      $ sbt assembly
      ```
         in the project directory. The resulted jar will be in:
         ```sh
       project-directory/target/scala-x.xx/project-name-assembly-x.x.jar
         ```
         
    - Submit the spark application using the command :
      ```sh
      $ spark-submit --class classname --master local[*] path-to-the-jar-file
      ```
	    
 - Spark Application: 
    - Import all used libraries.
    - Set Twitter4j configuration settings (Authentication Keys).
    - Set the key words for filtering.
    - Create Spark Streaming Context.
    - Linking spark streaming with twitter streaming, using authentication and filtering settings set previously.
    - Set transformations and actions that will be applied on the stream:  
            1. Get the full text of each tweet from Twitter API response:  
        Twitter API response is a stream of Twitter status in json structure. So it extract the text of the tweet of this json:  
          ```sh
          stream.foreachRDD{ rdd => rdd.map(x => x.getRetweetedStatus().getText())}
          ```   
          2. Remove Hashtags, URLs, retweeted flags, user mentions, duplicates spaces, and special characters from each of the texts using “clean” function:   
          ```sh
          stream.foreachRDD{ rdd => rdd.map(x => clean(x.getRetweetedStatus().getText()))	}
          ```  
          3. Pass each of these texts to the sentiment analysis function provided by Stanford CoreNLP library.              
		      ```sh
          stream.foreachRDD{ rdd => rdd.map(x => clean(x.getRetweetedStatus().getText())).foreach{ x => sentiment(x)} }
          ```
    - Start the stream. 
