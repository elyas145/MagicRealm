package controller.network.server.handlers;

import network.NetworkHandler;
import controller.ClientController;

public class EnterBirdSong implements NetworkHandler<ClientController> {

	@Override
	public void handle(ClientController controller) {
		controller.enterBirdSong();

	}

}
