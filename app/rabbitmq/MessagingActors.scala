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

//////////////////////////////////////////////////////////////////
import akka.actor._
import akka.actor.ActorSystem
import akka.actor.Props
import akka.event.ActorEventBus
import akka.event.LookupClassification
import akka.pattern.ask
import akka.util.Timeout
import akka.util.duration._

import play.api.Play.current

import com.rabbitmq.client.Channel
import com.rabbitmq.client.QueueingConsumer

import utils.Config
//////////////////////////////////////////////////////////////////

class SendingActor(channel: Channel, queue: String) extends Actor {
	def receive = {
		case some: String => {
			val msg = math.cos(System.currentTimeMillis).toString//(some + " : " + System.currentTimeMillis)
			channel.basicPublish("", queue, null, msg.getBytes)
		}
		case _ =>
	}
}

class ListeningActor(channel: Channel, queque: String, f: (String) => Any) extends Actor {

  val tEventBus = DataBusSingleton.getBus

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
					case some: String => {
						Turbine.insert(Turbine(anorm.NotAssigned,some))
						tEventBus.publish(MessageEvent(Config.TURBINE_CHANNEL,Message(some)))
						f(some)
					}
				}
			})) ! msg
		}
	}
	//def postStop() {} //TODO: disconnect from postgres and rabbitmq
}

case class Message(val msg:String)
case class MessageEvent(val channel:String, val msg:Message)

class TurbineDataEventBus extends ActorEventBus with LookupClassification {
	type Event 			= MessageEvent
	type Classifier = String

	protected def mapSize(): Int = { 10 }
	protected def classify(event: Event): Classifier = { event.channel }
	protected def publish(event: Event, subscriber: Subscriber): Unit = { subscriber ! event }
}

import controllers.Application._

class RetrieveActor extends Actor {
  def receive = {
    case some: MessageEvent => SimpleIterateeSingleton.updateEnumerator(some.msg.msg)
  }
}

object DataBusSingleton {
	private var dataBus: TurbineDataEventBus = null

	def getBus: TurbineDataEventBus = {
		dataBus match {
			case null => { 
				val turbineEventBus = new TurbineDataEventBus
				dataBus = turbineEventBus
				dataBus
			}
			case _ => {
				dataBus
			}
		}
	}
}
