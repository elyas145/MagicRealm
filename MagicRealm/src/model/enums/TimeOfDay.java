package model.enums;

public enum TimeOfDay {
	MIDNIGHT,
	DUSK,
	NOON,
	DAWN;
	public TimeOfDay next() {
		switch(this) {
		case MIDNIGHT:
			return DUSK;
		case DUSK:
			return NOON;
		case NOON:
			return DAWN;
		case DAWN:
			return MIDNIGHT;
		default:
			return MIDNIGHT;
		}
	}
}
