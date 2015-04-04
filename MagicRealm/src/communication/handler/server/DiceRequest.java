package communication.handler.server;

import client.ClientController;
import communication.ClientNetworkHandler;

public class DiceRequest implements ClientNetworkHandler{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6942451658958570076L;

	@Override
	public void handle(ClientController controller) {
		controller.rollDie();
		
	}
	
	@Override
	public String toString(){
		return "Die request handler.";		
	}

}
