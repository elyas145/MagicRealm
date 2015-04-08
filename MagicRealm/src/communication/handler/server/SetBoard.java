package communication.handler.server;

import client.ClientController;
import communication.ClientNetworkHandler;
import communication.handler.server.serialized.SerializedBoard;

public class SetBoard implements ClientNetworkHandler{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7435988302184072437L;
	private SerializedBoard board;
	
	public SetBoard(SerializedBoard board){
		this.board = board;
	}
	@Override
	public void handle(ClientController controller) {
		controller.initializeBoard(board);
	}

}
