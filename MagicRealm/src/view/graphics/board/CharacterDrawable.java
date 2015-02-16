package view.graphics.board;

import model.enums.CharacterType;

public abstract class CharacterDrawable extends ChitDrawable {

	public CharacterType getCharacterType() {
		return character;
	}

	protected CharacterDrawable(BoardDrawable bd, CharacterType ct) {
		super(bd, ct.toChit());
		character = ct;
	}

	private CharacterType character;

}
