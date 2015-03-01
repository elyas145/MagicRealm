package controller.network.server.handlers;

import network.NetworkHandler;
import controller.ClientController;

public class EnterCharacterSelection implements NetworkHandler<ClientController>{

	@Override
	public void handle(ClientController controller) {
		controller.enterCharacterSelection();
		
	}
	
}
