package controllers

import play.api._
import play.api.mvc._
import rabbitmq.Sender
import models.Turbine
import play.api.libs.iteratee._
import play.api.libs.concurrent._

object Application extends Controller {
  
  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def start = Action {
  	Sender.startSending
    Ok(views.html.index("AKKA STARTED"))
  }

  def test = Action {
  	Ok(views.html.index(Turbine.test.toString + " cock sucker"))
  }

  def live = WebSocket.using[String] { request => 
    
    // Just consume and ignore the input
    // val in = Iteratee.consume[String]()
    val in = Iteratee.foreach[String]( s => Logger.info(s) )

    // Send a single 'Hello!' message and close
    val out = Enumerator("fag juice!") //>>> Enumerator.eof
    
    (in, out)
  }
}
