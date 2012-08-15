import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "svc-windData"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
    	// "com.rabbitmq" % "amqp-client" % "2.7.0",
    	"com.rabbitmq" % "amqp-client" % "2.8.1",

    	"postgresql" % "postgresql" % "9.1-901-1.jdbc4"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      // Add your own project settings here      
    )

}

