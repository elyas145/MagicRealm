package communication.handler.server;

import communication.ClientNetworkHandler;

import client.ClientController;

public class EnterCharacterSelection implements ClientNetworkHandler{

	@Override
	public void handle(ClientController controller) {
		controller.enterCharacterSelection();
		
	}
	
}
