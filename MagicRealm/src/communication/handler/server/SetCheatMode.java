package communication.handler.server;

import client.ClientController;
import communication.ClientNetworkHandler;
import config.GameConfiguration;

public class SetCheatMode implements ClientNetworkHandler {

	private static final long serialVersionUID = 7400139523487991052L;

	@Override
	public void handle(ClientController controller) {
		GameConfiguration.Cheat = true;
	}

}
