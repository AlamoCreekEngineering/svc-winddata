package controllers

import play.api._
import play.api.mvc._
import rabbitmq.Sender
import models.Turbine
import play.api.libs.iteratee._
import play.api.libs.concurrent._

import utils.Config
import rabbitmq._

object Application extends Controller {
  
  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def start = Action {
  	Sender.startSending
    Ok(views.html.index("AKKA STARTED"))
  }

  def stop = Action {
    Sender.stopSending
    Ok("AKKA STOPPED")
  }


  def test = Action {
  	Ok(views.html.index(Turbine.test.toString + " cock sucker"))
  }

  def live = WebSocket.using[String] { request => 
    import akka.actor._
    import akka.util.duration._

    //open a websocket connection
    //ignore the incoming message and open a connection to the rabbitmq message stream for the most recent data

    val connection = RabbitMQConnection.getConnection
    val sendingChannel = connection.createChannel
    val f = (x: String) => Logger.info("LOGGED FROM WEBSOCKET "+x)
    val system = Config.RETRIEVAL_SYSTEM

    system.scheduler.schedule(
      2 seconds
      ,2 seconds
      ,system.actorOf(Props(new Sending1Actor(sendingChannel,Config.RABBITMQ_QUEUE)))
      ,""
    )

    // Sender.setupListener(sendingChannel, Config.RABBITMQ_QUEUE, f)


    //send the most recent data as long as there is some

    // val in = Iteratee.consume[String]()
    val in = Iteratee.foreach[String]( s => Logger.info(s) )

    // Send a single 'Hello!' message and close
    val out = Enumerator("fag juice!") //>>> Enumerator.eof
    
    // val out = system.actorOf(Props(new Actor {
    //   def receive = {
    //     case some: String => {
    //       Enumerator(some)
    //     }
    //   }
    // })) ! msg

    (in, out)
  }
}
