package communication.handler.server;

import client.ClientController;
import communication.ClientNetworkHandler;

public class CheckSwordsmanPlay implements ClientNetworkHandler {
	private static final long serialVersionUID = -8174217892766593866L;
	@Override
	public void handle(ClientController controller) {
		controller.checkSwordsmanTurn();
	}

}
