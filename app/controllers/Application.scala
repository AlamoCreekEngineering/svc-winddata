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

    private val (out, channel) = Concurrent.broadcast[JsValue]

    // private val out: PushEnumerator[JsValue] = Enumerator.imperative[JsValue]()
    private val in: Iteratee[JsValue,_] = Iteratee.foreach[JsValue] ( s => {
    Logger.info(s.toString); /*enum.push(s)*/ }).mapDone ( _ => () )

    def getOut: Enumerator[JsValue] = out
    def getIn: Iteratee[JsValue,_] = in

    def updateEnumerator(msg: String) = {
      channel.push(Json.toJson(Seq(Seq(Seq(1,2),Seq(4,5)))))
    }
  }
  
  def live = WebSocket.using[JsValue] { request => 

    val system = Config.RETRIEVAL_SYSTEM
    val turbineEventBus = DataBusSingleton.getBus
    val subscriber = system.actorOf(Props[RetrieveActor])
    turbineEventBus.subscribe(subscriber,Config.TURBINE_CHANNEL)

    (SimpleIterateeSingleton.getIn, SimpleIterateeSingleton.getOut)
  }
}
