package model.enums;

public enum SearchType {
	CLUES, NONE, PATHS, CLUES_PATHS, CHOICE, PASSAGES, PASSAGES_CLUES, DISCOVER_CHITS, LOOT, FAIL_LOOT;

	@Override
	public String toString() {
		switch (this) {
		case CLUES:
			return "Clues";
		case NONE:
			return "None";
		case PATHS:
			return "Paths";
		case CLUES_PATHS:
			return "Clues and Paths";
		case CHOICE:
			return "Choice";
		case PASSAGES:
			return "Passages";
		case PASSAGES_CLUES:
			return "Passages and Clues";
		case DISCOVER_CHITS:
			return "Discover Chits";
		case LOOT:
			return "Loot";
		default:
			return "None";
		}
	}
}
