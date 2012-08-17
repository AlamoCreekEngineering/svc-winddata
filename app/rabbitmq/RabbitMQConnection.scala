package rabbitmq

import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory

import utils.Config

object RabbitMQConnection {
	private val connection: Connection = null

	def getConnection: Connection = {
		connection match {
			case null => { 
				val factory = new ConnectionFactory
				factory.setUri(Config.RABBITMQ_URI)
				factory.newConnection
			}
			case _ => connection
		}
	}
}