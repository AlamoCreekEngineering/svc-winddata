package utils

import play.api.Play.current

object Config {
	val config = play.api.Play.configuration
	val RABBITMQ_URI = config.getString("rabbitmq.uri").getOrElse("localhost")
	val RABBITMQ_QUEUE = config.getString("rabbitmq.queue").getOrElse("queue")
  val RABBITMQ_EXCHANGE = config.getString("rabbit.exchange").getOrElse("exchange")
}