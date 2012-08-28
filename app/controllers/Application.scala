package controllers

import play.api._
import play.api.mvc._
import rabbitmq.Sender
import models.Turbine
import play.api.libs.iteratee._
import play.api.libs.concurrent._

import utils.Config
import rabbitmq._

import play.api.Play.current
import akka.actor.Props
import akka.pattern.ask
import akka.util.Timeout
import akka.util.duration._

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

  def live = WebSocket.async[String] { request => 
    RetrieveActor.output("very nice stuff")
  }

  import akka.actor.Actor
import play.api._
import play.api.mvc._

import play.api.libs.json._
import play.api.libs.iteratee._
import akka.actor._
import akka.util.duration._

import play.api._
import play.api.libs.json._
import play.api.libs.iteratee._
import play.api.libs.concurrent._

import akka.util.Timeout
import akka.pattern.ask

import play.api.Play.current
// import models._

import akka.actor._
import akka.util.duration._

  class RetrieveActor extends Actor {
    
    def receive = {
      case some: String => {
        sender ! some
      }
    }
  }

  object RetrieveActor {
  implicit val timeout = Timeout(1 second)

  lazy val default = {
    val retriever = Akka.system.actorOf(Props[RetrieveActor])
    retriever
  }

    def output(input: String): Promise[(Iteratee[String,_],Enumerator[String])] = {
          (default ? input).asPromise.map {

          case some: String => {
            Logger.info("faggity fag fag fag")

            val iteratee = Iteratee.foreach[String] { s => () }.mapDone { _ => () }

            // val iteratee = Done[String,_]((),Input.EOF)
            val enumerator =  Enumerator(some)
            (iteratee,enumerator)
          }
        }

    }

  }
}
