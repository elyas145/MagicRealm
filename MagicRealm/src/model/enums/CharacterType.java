package model.enums;

public enum CharacterType {
	AMAZON,
	CAPTAIN,
	SWORDSMAN;
	public ChitType toChit() {
		switch(this) {
		case AMAZON:
			return ChitType.CHARACTER_AMAZON;
		case CAPTAIN:
			return ChitType.CHARACTER_CAPTAIN;
		case SWORDSMAN:
			return ChitType.CHARACTER_SWORDSMAN;
		default:
			return ChitType.NONE;
		}
	}
}
