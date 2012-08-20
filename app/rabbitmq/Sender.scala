package rabbitmq

import akka.actor._
import akka.util.duration._

import com.rabbitmq.client.Channel

import play.api.Play.current
import play.api._
import play.api.libs.concurrent._
// import play.api.libs.concurrent.Akka.system

import utils.Config

object Sender {

	def startSending = {
		val connection = RabbitMQConnection.getConnection
		val sendingChannel = connection.createChannel
	
		sendingChannel.queueDeclare(Config.RABBITMQ_QUEUE, false, false, false, null)

		val f = (x: String) => Logger.info("Received on queue from FIRST CALLBACK: "+x)

		setupListener(connection.createChannel, Config.RABBITMQ_QUEUE, f)

		Akka.system.scheduler.schedule(2 seconds, 1 seconds
			, Akka.system.actorOf(Props(
					new SendingActor(channel = sendingChannel, queue = Config.RABBITMQ_QUEUE)))
			, "")
	}

	private def setupListener(receiveChannel: Channel, queue: String, f: (String) => Any) {
		Akka.system.scheduler.scheduleOnce(2 seconds
			, Akka.system.actorOf(Props(new ListeningActor(receiveChannel, queue, f)))
			, "")
	}
}
