package controllers

import play.api._
import play.api.mvc._

object Application extends Controller {
  
  def index = Action {
  	import play.api.Play.current
  	val config = play.api.Play.configuration
    val s = config.getString("rabbitmq.uri").getOrElse("localhost")
  	Console.println(s)
  	Logger.info("ballz")
    Ok(views.html.index("Your new application is ready."))
  }
  
}


