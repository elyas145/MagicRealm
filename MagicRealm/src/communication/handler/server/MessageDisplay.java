package communication.handler.server;

import communication.NetworkHandler;

import client.ClientController;

public class MessageDisplay implements NetworkHandler {
	private static final long serialVersionUID = -5820183512389136335L;
	private String message;
	
	public MessageDisplay(String msg){
		message = msg;
	}
	@Override
	public void handle(ClientController controller) {		
		controller.displayMessage(message);
	}

}
