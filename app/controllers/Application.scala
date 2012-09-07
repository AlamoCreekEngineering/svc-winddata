package controllers

import play.api._
import play.api.mvc._
import rabbitmq.Sender
import models.Turbine
import play.api.libs.iteratee._
import play.api.libs.concurrent._

import utils.Config
import rabbitmq._

//////////////////////////////////////////////////////////////////
import akka.actor._
import akka.actor.ActorSystem
import akka.actor.Props
import akka.event.ActorEventBus
import akka.event.LookupClassification
import akka.pattern.ask
import akka.util.Timeout
import akka.util.duration._
//////////////////////////////////////////////////////////////////

import play.api.libs.json._

object Application extends Controller {
  
  def index = Action {
    Ok(views.html.index("FUCK EVERYTHING YOU THOUGHT YOU KNEW"))
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

  object SimpleIterateeSingleton {

    private val (out, channel) = Concurrent.broadcast[String]

    // private val out: PushEnumerator[JsValue] = Enumerator.imperative[JsValue]()
    private val in: Iteratee[String,_] = Iteratee.foreach[String] ( s => {
    Logger.info(s.toString); /*enum.push(s)*/ }).mapDone ( _ => () )

    def getOut: Enumerator[String] = out
    def getIn: Iteratee[String,_] = in

    def updateEnumerator(msg: String) = {
      import com.codahale.jerkson.Json._
      import scala.util.Random
      val r = new Random
      val json = generate( Map( "x" -> r.nextInt(10), "y" -> r.nextInt(10) ) )
      // channel.push(Json.toJson(Seq(Seq(Seq(1,2),Seq(4,5)))))
      channel.push(json)
    }
  }
  
  def live = WebSocket.using[String] { request => 

    val system = Config.RETRIEVAL_SYSTEM
    val turbineEventBus = DataBusSingleton.getBus
    val subscriber = system.actorOf(Props[RetrieveActor])
    turbineEventBus.subscribe(subscriber,Config.TURBINE_CHANNEL)

    (SimpleIterateeSingleton.getIn, SimpleIterateeSingleton.getOut)
  }
}
