package view.graphics;

import model.board.Board;

public abstract class BoardDrawable implements Drawable {
	
	public Board getBoard() {
		return board;
	}
	
	protected BoardDrawable(Board bo) {
		board = bo;
	}
	
	private Board board;

}
