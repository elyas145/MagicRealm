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
			logger = new ConsoleLogger();
			logger.start();
			server = new Server<Logger, Logger>(new Invoker<Logger>() {
				
				@Override
				public void invoke(Handler<Logger> handle) {
					logger.log("Invoking a handler from the client");
					handle.handle(logger);
					logger.log("Sending a response to the client");
					server.send(new LoggerHandler("Server says: 'Hey client!'"), clientID);
				}

			});
			server.setConnectionHandler(new Handler<Integer>() {

				@Override
				public void handle(Integer id) {
					logger.log("Server accepted connection from " + id);
					clientID = id;
				}
				
			});
			server.setDroppedConnectionHandler(new Handler<Integer>() {

				@Override
				public void handle(Integer object) {
					logger.log("Server dropped the connection from " + object);
				}
				
			});
			server.start();
			client = new Client<Logger, Logger>(logger, "localhost");
			client.start();
			client.send(new LoggerHandler("Client says: 'Hi server!'"));
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			client.stop();
			server.stop();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
