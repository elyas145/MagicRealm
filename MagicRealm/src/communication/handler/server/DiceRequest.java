package communication.handler.server;

import client.ClientController;
import communication.ClientNetworkHandler;

public class DiceRequest implements ClientNetworkHandler{

	@Override
	public void handle(ClientController controller) {
		controller.rollDie();
		
	}

}
