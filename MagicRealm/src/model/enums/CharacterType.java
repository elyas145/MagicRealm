package model.enums;

public enum CharacterType {
	AMAZON,
	CAPTAIN,
	SWORDSMAN;
	public CounterType toCounter() {
		switch(this) {
		case AMAZON:
			return CounterType.CHARACTER_AMAZON;
		case CAPTAIN:
			return CounterType.CHARACTER_CAPTAIN;
		case SWORDSMAN:
			return CounterType.CHARACTER_SWORDSMAN;
		default:
			return CounterType.NONE;
		}
	}
}
