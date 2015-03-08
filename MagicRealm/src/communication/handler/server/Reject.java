package communication.handler.server;

import communication.NetworkHandler;

import client.ClientController;

public class Reject implements NetworkHandler{
	private static final long serialVersionUID = 8378725316704524987L;

	@Override
	public void handle(ClientController controller) {
		controller.displayMessage("You were rejected from the game. please try again at a later time.");		
	}
	
}
