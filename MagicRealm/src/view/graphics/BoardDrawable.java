package view.graphics;

import model.board.Board;

public abstract class BoardDrawable {
	
	public Board getBoard() {
		return board;
	}
	
	protected BoardDrawable(Board bo) {
		board = bo;
	}
	
	private Board board;

}
