package communication.handler.server;

import communication.ClientNetworkHandler;

import client.ClientController;

public class EnterCharacterSelection implements ClientNetworkHandler{

	/**
	 * 
	 */
	private static final long serialVersionUID = -451149504635937593L;

	@Override
	public void handle(ClientController controller) {
		controller.enterCharacterSelection();
		
	}
	
}
