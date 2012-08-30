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

import akka.util.duration._
  def live = WebSocket.using[String] { request => 

    val iteratee = Iteratee.foreach[String] ( s => () ).mapDone ( _ => () )

    val timeStream = Enumerator.fromCallback { () => 
      Promise.timeout(Some("dirty cock"), 2 seconds)
    }

    (iteratee,timeStream)
    // RetrieveActor.output("very nice stuff")
  }

import akka.actor._
import akka.actor.ActorSystem
import akka.actor.Props
import akka.event.ActorEventBus
import akka.event.LookupClassification
import akka.pattern.ask
import akka.util.Timeout
import akka.util.duration._


  // def live = Action {
  //   val system = Config.RETRIEVAL_SYSTEM
  //   val turbineEventBus = DataBusSingleton.getBus
  //   val TURBINE_CHANNEL = "/turbine/current"
  //   val subscriber = system.actorOf(Props[RetrieveActor])
  //   turbineEventBus.subscribe(subscriber,TURBINE_CHANNEL)
  //   // RetrieveActor.subscribe
  //   Logger.info(subscriber+ " <<<< SUCK IT BITCH")
  //   Ok("LIVE")
  // }

}
