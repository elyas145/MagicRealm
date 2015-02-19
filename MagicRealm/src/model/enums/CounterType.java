package model.enums;

public enum CounterType {
	CHARACTER_AMAZON,
	CHARACTER_CAPTAIN,
	CHARACTER_SWORDSMAN,
	SITE_STATUE,
	SITE_ALTAR,
	SITE_VAULT,
	SITE_POOL,
	SITE_LOST_CITY,
	SITE_HOARD,
	SITE_LAIR,
	SITE_CAIRNS,
	SITE_SHRINE,
	SITE_LOST_CASTLE,
	SITE_CHAPEL,
	SITE_GUARD_HOUSE,
	SITE_HOUSE,
	SITE_HUT,
	SITE_INN,
	SITE_LARGE_FIRE,
	SITE_SMALL_FIRE,
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
		case SITE_STATUE:
		case SITE_ALTAR:
		case SITE_VAULT:
		case SITE_POOL:
		case SITE_LOST_CITY:
		case SITE_HOARD:
		case SITE_LAIR:
		case SITE_CAIRNS:
		case SITE_SHRINE:
		case SITE_LOST_CASTLE:
		case SITE_CHAPEL:
		case SITE_GUARD_HOUSE:
		case SITE_HOUSE:
		case SITE_HUT:
		case SITE_INN:
		case SITE_LARGE_FIRE:
		case SITE_SMALL_FIRE:
			return true;
		default:
			return false;
		}
	}
}
