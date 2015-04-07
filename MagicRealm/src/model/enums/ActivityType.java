package model.enums;

public enum ActivityType {
	NONE,
	MOVE,
	HIDE,
	SEARCH,
	ENCHANT;
	
	@Override
	public String toString() {
		switch(this) {
		case NONE:
			return "None";
		case MOVE:
			return "Move";
		case HIDE:
			return "Hide";
		case SEARCH:
			return "Search";
		case ENCHANT:
			return "Enchant";
		}
		return "null";
	}
}
