package network.main;

import java.io.IOException;

import utils.handler.Handler;
import utils.handler.Invoker;
import utils.logger.ConsoleLogger;
import utils.logger.Logger;
import network.NetworkHandler;
import network.client.Client;
import network.server.Server;

public class NetworkTester {
	
	// serializable handler of Logger objects that logs the message
	static class LoggerHandler implements NetworkHandler<Logger> {
		
		private static final long serialVersionUID = 6453692154486461928L;

		String message;
		
		public LoggerHandler(String msg) {
			message = msg;
		}

		@Override
		public void handle(Logger object) {
			object.log(message);
		}
		
	}
	
	static ConsoleLogger logger;
	
	static Server<Logger, Logger> server;
	
	static Client<Logger, Logger> client;
	
	static int clientID;

	public static void main(String[] args) {
		try {
			// initialize and start the logger
			logger = new ConsoleLogger();
			logger.start();
			// create a new server
			server = new Server<Logger, Logger>(new Invoker<Logger>() {
				
				// this invoker gives feedback about the operations
				@Override
				public void invoke(Handler<Logger> handle) {
					logger.log("Invoking a handler from the client");
					handle.handle(logger);
					logger.log("Sending a response to the client");
					server.send(new LoggerHandler("Server says: 'Hey client!'"), clientID);
				}

			});
			// set the handler for new connections
			server.setConnectionHandler(new Handler<Integer>() {

				@Override
				public void handle(Integer id) {
					logger.log("Server accepted connection from " + id);
					clientID = id;
				}
				
			});
			// set the handler for dropped connections
			server.setDroppedConnectionHandler(new Handler<Integer>() {

				@Override
				public void handle(Integer object) {
					logger.log("Server dropped the connection from " + object);
				}
				
			});
			// start the server
			server.start();
			// create a new client
			client = new Client<Logger, Logger>(logger, "localhost");
			// start the client
			client.start();
			// send a message
			client.send(new LoggerHandler("Client says: 'Hi server!'"));
			// stop and wait for the client to finish
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			client.stop();
			client.join();
			// stop and wait for the server to finish
			server.stop();
			server.join();
			// stop and wait for the logger to finish
			logger.log("Stopping logger");
			logger.stop();
			logger.join();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
