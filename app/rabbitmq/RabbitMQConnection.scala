package rabbitmq

import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory

import utils.Config

object RabbitMQConnection {
	private var connection: Connection = null

	def getConnection: Connection = {
		connection match {
			case null => { 
				val factory = new ConnectionFactory
				factory.setUri(Config.RABBITMQ_URI)
				connection = factory.newConnection
				connection
			}
			case _ => {
				connection
			}
		}
	}
}