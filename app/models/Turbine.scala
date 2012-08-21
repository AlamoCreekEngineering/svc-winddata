package models

import anorm._
import play.api.db.DB
import play.api.Play.current

case class Turbine(id: Pk[Long] = NotAssigned, watts: String)

object Turbine {
	def test: Boolean = {
			DB.withConnection { implicit connection =>
			val r: Boolean = SQL("SELECT * FROM Turbine").execute()
			r
		}
	}

	def insert(turbine: Turbine) = {
		DB.withConnection { implicit connection =>
			SQL(
				"""
					INSERT INTO TURBINE(watts) VALUES ({watts})
				"""
				).on('watts -> turbine.watts).executeUpdate()
		}
	}

}
