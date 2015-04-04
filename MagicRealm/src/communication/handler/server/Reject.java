package communication.handler.server;

import communication.ClientNetworkHandler;
import client.ClientController;

public class Reject implements ClientNetworkHandler{
	private static final long serialVersionUID = 8378725316704524987L;

	@Override
	public void handle(ClientController controller) {
		controller.displayMessage("You were rejected from the game. please try again at a later time.");		
	}
	@Override
	public String toString(){
		return "reject handler.";		
	}
	
}
