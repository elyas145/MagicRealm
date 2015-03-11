package communication.handler.server;

import client.ClientController;
import server.ServerController;
import communication.ClientNetworkHandler;

public class StartGame implements ClientNetworkHandler {
	private SerializedBoard board;

	public StartGame(SerializedBoard sboard) {
		board = sboard;
	}

	@Override
	public void handle(ClientController controller) {
		controller.startGame(board);
		
	}

}
