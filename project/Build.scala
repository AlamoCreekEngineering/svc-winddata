import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "svc-windData"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
    	"com.rabbitmq" % "amqp-client" % "2.7.0",
    	"postgresql" % "postgresql" % "9.0-801.jdbc3"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      // Add your own project settings here      
    )

}

