package model.enums;

public enum TileName {
	AWFUL_VALLEY, // valley
	BAD_VALLEY, // valley
	BORDERLAND, // cave
	CAVERN, // cave
	CAVES, // cave
	CLIFF, // mountain
	CRAG, // mountain
	CURST_VALLEY, // valley
	DARK_VALLEY, // valley
	DEEP_WOODS, // woods
	EVIL_VALLEY, // valley
	HIGH_PASS, // cave
	LEDGES, // mountain
	LINDEN_WOODS, // woods
	MAPLE_WOODS, // woods
	MOUNTAIN, // mountain
	NUT_WOODS, // woods
	OAK_WOODS, // woods
	PINE_WOODS, // woods
	RUINS, // cave
	EMPTY;
	public LandType getType() {
		switch(this) {
		case BORDERLAND:
		case CAVERN:
		case CAVES:
		case HIGH_PASS:
		case RUINS:
			return LandType.CAVE;
		case CLIFF:
		case CRAG:
		case LEDGES:
		case MOUNTAIN:
			return LandType.MOUNTAIN;
		case AWFUL_VALLEY:
		case BAD_VALLEY:
		case CURST_VALLEY:
		case DARK_VALLEY:
		case EVIL_VALLEY:
			return LandType.VALLEY;
		case DEEP_WOODS:
		case LINDEN_WOODS:
		case MAPLE_WOODS:
		case NUT_WOODS:
		case OAK_WOODS:
		case PINE_WOODS:
			return LandType.WOODS;
		default:
			return LandType.NONE;
		}
	}
}
