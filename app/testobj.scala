import play.api._
import controllers.Application._

// object Global extends GlobalSettings {

//   override def onStart(app: Application) {
//     Logger.info("Application has started and it has a rabbit boner")
//   }  
  
//   override def onStop(app: Application) {
//     Logger.info("Application shutdown...")
//   }  
    
// }

object Global extends GlobalSettings {

	override def onStart(app: Application) {
			Logger.info("IM BEING CALLED I SWEAR")

		controllers.Sender.startSending
	}
}