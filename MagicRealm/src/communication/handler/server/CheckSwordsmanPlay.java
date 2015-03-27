package communication.handler.server;

import client.ClientController;
import server.ServerController;
import communication.ClientNetworkHandler;
import communication.ServerNetworkHandler;

public class CheckSwordsmanPlay implements ClientNetworkHandler{
	private static final long serialVersionUID = 1652736329815439402L;

	@Override
	public void handle(ClientController controller) {
		controller.checkSwordsmanTurn();
	}

}
