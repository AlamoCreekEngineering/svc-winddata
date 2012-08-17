package rabbitmq

import akka.actor.Props
import akka.util.duration.intToDurationInt

import com.rabbitmq.client.Channel

import play.api._
import play.libs.Akka

import utils.Config

object Sender {
	def startSending = {
		val connection = RabbitMQConnection.getConnection
		val sendingChannel = connection.createChannel
		sendingChannel.queueDeclare(Config.RABBITMQ_QUEUE, false, false, false, null)

		val firstCallBack = (x: String) => Logger.info("Received on queue from FIRST CALLBACK: "+x)
		setupListener(connection.createChannel, Config.RABBITMQ_QUEUE, firstCallBack)

		val secondCallBack = (x: String) => Logger.info("Received on queue from SECOND CALLBACK: "+x)
		setupListener(connection.createChannel, Config.RABBITMQ_QUEUE, secondCallBack)

		Akka.system.scheduler.schedule(2 seconds, 1 seconds
			, Akka.system.actorOf(Props(
					new SendingActor(channel = sendingChannel, queue = Config.RABBITMQ_QUEUE)))
			, "CHOCOLATE SALTY BALLZ")
	}

	private def setupListener(receiveChannel: Channel, queue: String, f: (String) => Any) {
		Akka.system.scheduler.scheduleOnce(2 seconds
			, Akka.system.actorOf(Props(new ListeningActor(receiveChannel, queue, f)))
			, "BLANK")
	}
}