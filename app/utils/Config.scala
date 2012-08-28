package utils

import play.api.Play.current
import akka.actor._

object Config {
	val config = play.api.Play.configuration
	val RABBITMQ_URI = config.getString("rabbitmq.uri").getOrElse("localhost")
	val RABBITMQ_QUEUE = config.getString("rabbitmq.queue").getOrElse("queue")
  val RABBITMQ_EXCHANGE = config.getString("rabbit.exchange").getOrElse("exchange")
  val STORAGE_SYSTEM = ActorSystem("Storage")
  val RETRIEVAL_SYSTEM = ActorSystem("Retrieval")
}