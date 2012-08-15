package controllers

import play.api._
import play.api.mvc._
import play.api.Play.current

object Application extends Controller {
  
  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

}

object Config {
	val config = play.api.Play.configuration
	val RABBITMQ_URI = config.getString("rabbitmq.uri").getOrElse("localhost")
	val RABBITMQ_QUEUE = config.getString("rabbitmq.queue").getOrElse("queue")
  val RABBITMQ_EXCHANGE = config.getString("rabbit.exchange").getOrElse("exchange")
}

object RabbitMQConnection {
	import com.rabbitmq.client.Connection
  import com.rabbitmq.client.ConnectionFactory

	private val connection: Connection = null

	def getConnection: Connection = {
		connection match {
			case null => { 
				val factory = new ConnectionFactory
				factory.setUri(Config.RABBITMQ_URI)
				factory.newConnection
			}
			case _ => connection
		}
	}
}


// import play.api.libs.concurrent.Akka
import play.api.libs.concurrent._
import akka.util.duration.intToDurationInt
import akka.actor.Actor

import akka.actor.actorRef2Scala
import akka.actor.Actor
import akka.actor.Props
import akka.dispatch.Await
import akka.pattern.ask
import akka.util.duration.intToDurationInt
import akka.util.Timeout
import play.libs.Akka

	import com.rabbitmq.client.Channel
	import com.rabbitmq.client.Connection
  import com.rabbitmq.client.ConnectionFactory
  import com.rabbitmq.client.QueueingConsumer

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

class SendingActor(channel: Channel, queue: String) extends Actor {
	def receive = {
		case some: String => {
			val msg = (some + " : " + System.currentTimeMillis)
			channel.basicPublish("", queue, null, msg.getBytes)
			Logger.info(msg)
		}
		case _ => 
	}
}

class ListeningActor(channel: Channel, queque: String, f: (String) => Any) extends Actor {
	def receive = {
		case _ => startReceiving
	}

	def startReceiving = {
		val consumer = new QueueingConsumer(channel)
		channel.basicConsume(queque, true, consumer)
		while(true) {
			val delivery = consumer.nextDelivery
			val msg = new String(delivery.getBody)

			context.actorOf(Props(new Actor {
				def receive = {
					case some:String => f(some)
				}
			})) ! msg
		}
	}
}


