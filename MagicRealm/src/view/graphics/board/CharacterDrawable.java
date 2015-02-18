package view.graphics.board;

import model.enums.CharacterType;

public abstract class CharacterDrawable extends CounterDrawable {

	public CharacterType getCharacterType() {
		return character;
	}

	protected CharacterDrawable(CharacterType ct) {
		super(ct.toCounter());
		character = ct;
	}

	private CharacterType character;

}
