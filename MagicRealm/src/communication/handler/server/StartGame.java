package communication.handler.server;

import client.ClientController;
import communication.ClientNetworkHandler;
import communication.handler.server.serialized.SerializedBoard;

public class StartGame implements ClientNetworkHandler {
	private static final long serialVersionUID = 1093417167119495512L;
	private SerializedBoard board;

	public StartGame(SerializedBoard sboard) {
		board = sboard;
	}

	@Override
	public void handle(ClientController controller) {
		controller.startGame(board);
		
	}

}
