package controllers

import play.api._
import play.api.mvc._
import rabbitmq.Sender
import models.Turbine

object Application extends Controller {
  
  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def start = Action {
  	Sender.startSending
    Ok(views.html.index("AKKA STARTED"))
  }

  def test = Action {
  	Ok(views.html.index(Turbine.test.toString + "cock sucker"))
  }

}
