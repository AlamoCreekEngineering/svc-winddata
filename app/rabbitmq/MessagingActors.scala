package rabbitmq

import akka.actor.Actor
import akka.actor.Props

import com.rabbitmq.client.Channel
import com.rabbitmq.client.QueueingConsumer
import play.api.libs.concurrent._

import play.api._
import play.libs.Akka

import models.Turbine
import anorm._ 

class SendingActor(channel: Channel, queue: String) extends Actor {
	def receive = {
		case some: String => {
			Logger.info(some + "====================")
			val msg = math.cos(System.currentTimeMillis).toString//(some + " : " + System.currentTimeMillis)
			channel.basicPublish("", queue, null, msg.getBytes)
		}
		case _ =>
	}
}

class Sending1Actor(channel: Channel, queue: String) extends Actor {
	def receive = {
		case some: String => {
			Logger.info(some + "====================")
			val msg = math.cos(System.currentTimeMillis).toString//(some + " : " + System.currentTimeMillis)
			channel.basicPublish("", queue, null, msg.getBytes)
		}
		case _ =>
	}
}

class ListeningActor(channel: Channel, queque: String, f: (String) => Any) extends Actor {

	def receive = {
		case _ => {
			startReceiving
		}
	}

	def startReceiving = {
		val consumer = new QueueingConsumer(channel)
		channel.basicConsume(queque, true, consumer)
		while(true) {
			val delivery = consumer.nextDelivery
			val msg = new String(delivery.getBody)

			context.actorOf(Props(new Actor {
				def receive = {
					case some: String => {
						Turbine.insert(Turbine(anorm.NotAssigned,some))
						f(some)
					}
				}
			})) ! msg
		}
	}
}