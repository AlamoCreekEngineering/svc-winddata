package rabbitmq

import akka.actor.Actor
import akka.actor.Props

import com.rabbitmq.client.Channel
import com.rabbitmq.client.QueueingConsumer

import play.api._
import play.libs.Akka

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