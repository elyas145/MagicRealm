package communication.handler.server;

import communication.ClientNetworkHandler;

import client.ClientController;
import view.controller.game.BoardView;
import model.board.Board;

public class InitBoard implements ClientNetworkHandler{
	private Board board;
	
	public InitBoard(Board initializer){
		board = initializer;
	}
	@Override
	public void handle(ClientController controller) {
		controller.initializeBoard(board);
		
	}

}
