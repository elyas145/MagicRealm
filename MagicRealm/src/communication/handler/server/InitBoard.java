package communication.handler.server;

import communication.ClientNetworkHandler;

import client.ClientController;

public class InitBoard implements ClientNetworkHandler{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8394627111897586695L;
	private SerializedBoard board;
	
	public InitBoard(SerializedBoard sboard){
		board = sboard;
	}
	@Override
	public void handle(ClientController controller) {
		controller.initializeBoard(board);
		
	}

}
