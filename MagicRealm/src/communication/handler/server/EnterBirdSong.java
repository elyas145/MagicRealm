package communication.handler.server;

import communication.NetworkHandler;

import client.ClientController;

public class EnterBirdSong implements NetworkHandler {

	@Override
	public void handle(ClientController controller) {
		controller.enterBirdSong();

	}

}
