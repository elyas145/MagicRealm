package model.enums;

public enum CounterType {
	CHARACTER_AMAZON,
	CHARACTER_CAPTAIN,
	CHARACTER_SWORDSMAN,
	VALLEY_CHAPEL,
	VALLEY_GUARD_HOUSE,
	VALLEY_HOUSE,
	VALLEY_INN,
	NONE;
	public boolean isCharacter() {
		switch(this) {
		case CHARACTER_AMAZON:
		case CHARACTER_CAPTAIN:
		case CHARACTER_SWORDSMAN:
			return true;
		default:
			return false;
		}
	}
	public boolean isValley() {
		switch(this) {
		case VALLEY_CHAPEL:
		case VALLEY_GUARD_HOUSE:
		case VALLEY_HOUSE:
		case VALLEY_INN:
			return true;
		default:
			return false;
		}
	}
}
