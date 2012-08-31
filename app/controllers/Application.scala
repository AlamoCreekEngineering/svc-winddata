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

  object SimpleIterateeSingleton {
    val enum: PushEnumerator[String] = Enumerator.imperative[String]()
    val iter: Iteratee[String,_] = Iteratee.foreach[String] ( s => {
      Logger.info(s); /*enum.push(s)*/ }).mapDone ( _ => () )

    def updateEnum(msg: String) = {
      Logger.debug("updating enum")
      // Enumerator("cock surprise") >>> enum
      enum.push(msg)
      // enum |>> iter
    }
  }

  import akka.util.duration._
  
  def live = WebSocket.using[String] { request => 

    val system = Config.RETRIEVAL_SYSTEM
    val turbineEventBus = DataBusSingleton.getBus
    val TURBINE_CHANNEL = "/turbine/current"
    val subscriber = system.actorOf(Props[RetrieveActor])
    turbineEventBus.subscribe(subscriber,TURBINE_CHANNEL)

    val out = SimpleIterateeSingleton.enum
    val in = SimpleIterateeSingleton.iter


    // val in = Iteratee.foreach[String](msg => out.push(msg)).mapDone {
    //   x => {
    //     Logger.debug("Connection Closed")
    //     "DONE"
    //   }
    // }

    // Promise.timeout(Logger.debug("Here's the first line"), 2 seconds)      

    
    (in, out)
    // val iteratee = Iteratee.foreach[String] ( s => () ).mapDone ( _ => () )

    // val timeStream = Enumerator.fromCallback { () => 
    //   Promise.timeout(Some("dirty cock"), 2 seconds)      
    // }

    // (iteratee,timeStream)
    // // RetrieveActor.output("very nice stuff")
  }

class SimpleConnection extends akka.actor.Actor { 
        var outbound: Option[PushEnumerator[String]] = None 
        var inbound: Option[Iteratee[String, Unit]] = None 
        def receive = { 
                case("start connection", connType: String) => { 
                        connType match { 
                                case "websocket" => { 

  //                                 Enumerator.imperative[String] (
  // onStart = { () => you write your code here },
  // onComplete = { () => /*you write your code here*/ }
  // )
                                        outbound = Some(Enumerator.imperative[String]())

                                        inbound = Some(Iteratee.foreach[String](event => self ! event).mapDone { 
                                                _ => { 
                                                        outbound.get >>> Enumerator.eof 
                                                        context.stop(self) 
                                                } 
                                        }) 
                                        sender ! (inbound.get, outbound.get) 
                                } 
                                case "comet" => { 
                                        outbound = Some(Enumerator.imperative[String]()) 
                                        sender ! outbound.get 
                                } 
                        } 
                } 
                case js: String => { 
                        //do something with inbound JSON from the browser here 
                } 
        } 
} 
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
