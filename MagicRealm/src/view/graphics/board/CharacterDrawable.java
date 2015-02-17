package view.graphics.board;

import model.enums.CharacterType;

public abstract class CharacterDrawable extends CounterDrawable {

	public CharacterType getCharacterType() {
		return character;
	}

	protected CharacterDrawable(BoardDrawable bd, CharacterType ct) {
		super(bd, ct.toCounter());
		character = ct;
	}

	private CharacterType character;

}
