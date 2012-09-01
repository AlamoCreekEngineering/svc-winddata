package rabbitmq

import akka.actor._
import akka.util.duration._

import com.rabbitmq.client.Channel

import play.api.Play.current
import play.api._
import play.api.libs.concurrent._

import utils.Config

object Sender {

	val system = Config.STORAGE_SYSTEM

	def startSending = {

		val connection = RabbitMQConnection.getConnection
		val sendingChannel = connection.createChannel
		sendingChannel.queueDeclare(Config.RABBITMQ_QUEUE, false, false, false, null)

		system.scheduler.schedule(
			2 seconds
			,2 seconds
			,system.actorOf(Props(new SendingActor(sendingChannel,Config.RABBITMQ_QUEUE)))
			,""
		)

		val f = (x: String) => Logger.info("Received on queue from FIRST CALLBACK: "+x)
		setupListener(sendingChannel, Config.RABBITMQ_QUEUE, f)

	}

	def setupListener(receiveChannel: Channel, queue: String, f: (String) => Any) {
		system.scheduler.scheduleOnce(
			2 seconds
			,system.actorOf(Props(new ListeningActor(receiveChannel, queue, f)))
			,""
		)
	}

	def stopSending = {
    val system = Config.STORAGE_SYSTEM
		system.shutdown
	}
}