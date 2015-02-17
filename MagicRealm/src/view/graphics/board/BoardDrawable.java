package view.graphics.board;

import view.graphics.Drawable;
import model.board.Board;
import model.enums.CounterType;
import model.enums.TileName;

public abstract class BoardDrawable implements Drawable {
	
	public Board getBoard() {
		return board;
	}

	public abstract boolean isTileEnchanted(TileName name);
	
	public abstract void setCounter(CounterType counter, TileName tile, int clearing);
	
	protected BoardDrawable(Board bo) {
		board = bo;
	}
	
	private Board board;

}
