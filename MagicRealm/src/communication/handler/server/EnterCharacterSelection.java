package communication.handler.server;

import communication.NetworkHandler;

import client.ClientController;

public class EnterCharacterSelection implements NetworkHandler{

	@Override
	public void handle(ClientController controller) {
		controller.enterCharacterSelection();
		
	}
	
}
