package model.enums;

public enum CounterType {
	CHARACTER_AMAZON,
	CHARACTER_CAPTAIN,
	CHARACTER_SWORDSMAN,
	SITE_CHAPEL,
	SITE_GUARD_HOUSE,
	SITE_HOUSE,
	SITE_INN,
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
	public boolean isSite() {
		switch(this) {
		case SITE_CHAPEL:
		case SITE_GUARD_HOUSE:
		case SITE_HOUSE:
		case SITE_INN:
			return true;
		default:
			return false;
		}
	}
}
