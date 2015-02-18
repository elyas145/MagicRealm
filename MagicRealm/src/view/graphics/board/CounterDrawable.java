package view.graphics.board;

import view.graphics.Drawable;
import model.enums.CounterType;

public abstract class CounterDrawable implements Drawable {

	public CounterType getCounterType() {
		return type;
	}

	protected CounterDrawable(CounterType ct) {
		type = ct;
	}

	private CounterType type;

}
