package controller.network.server.handlers;

import network.NetworkHandler;
import controller.ClientController;

public class MessageDisplay implements NetworkHandler<ClientController> {
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
