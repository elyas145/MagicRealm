package controller.network.server.handlers;

import view.controller.game.BoardView;
import model.board.Board;
import network.NetworkHandler;
import controller.ClientController;

public class InitBoard implements NetworkHandler<ClientController>{
	private NetworkHandler<BoardView> board;
	
	public InitBoard(NetworkHandler<BoardView> initializer){
		board = initializer;
	}
	@Override
	public void handle(ClientController controller) {
		controller.initializeBoard(board);
		
	}

}
