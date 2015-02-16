package view.graphics.board;

import model.enums.ChitType;

public abstract class ChitDrawable extends BoardItemDrawable {

	public ChitType getChitType() {
		return type;
	}

	protected ChitDrawable(BoardDrawable parent, ChitType ct) {
		super(parent);
		type = ct;
	}

	private ChitType type;

}
