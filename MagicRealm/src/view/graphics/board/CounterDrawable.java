package view.graphics.board;

import model.enums.CounterType;

public abstract class CounterDrawable extends BoardItemDrawable {

	public CounterType getCounterType() {
		return type;
	}

	protected CounterDrawable(BoardDrawable parent, CounterType ct) {
		super(parent);
		type = ct;
	}

	private CounterType type;

}
