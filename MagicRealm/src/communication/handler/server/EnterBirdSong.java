package communication.handler.server;

import communication.ClientNetworkHandler;

import client.ClientController;

public class EnterBirdSong implements ClientNetworkHandler {

	@Override
	public void handle(ClientController controller) {
		controller.enterBirdSong();

	}

}
