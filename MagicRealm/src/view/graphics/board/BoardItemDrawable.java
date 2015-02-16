package view.graphics.board;

import view.graphics.Drawable;

public abstract class BoardItemDrawable implements Drawable {
	
	protected BoardItemDrawable(BoardDrawable bd) {
		board = bd;
	}
	
	protected BoardDrawable getParent() {
		return board;
	}
	
	private BoardDrawable board;

}
