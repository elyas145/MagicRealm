package communication.handler.server;

import communication.ClientNetworkHandler;
import communication.handler.server.serialized.SerializedBoard;
import client.ClientController;

public class InitBoard implements ClientNetworkHandler{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8394627111897586695L;
	
	
	public InitBoard(SerializedBoard sboard){
		//TODO initialize the players, dwellings and map chits
	}
	@Override
	public void handle(ClientController controller) {
		//controller.initializeBoard(board);
		
	}

}
