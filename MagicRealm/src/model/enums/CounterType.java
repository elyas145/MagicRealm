package model.enums;

public enum CounterType {
	CHARACTER_AMAZON,
	CHARACTER_CAPTAIN,
	CHARACTER_SWORDSMAN,
	VALLEY_CHAPEL,
	VALLEY_GUARD_HOUSE,
	VALLEY_HOUSE,
	VALLEY_INN,
	SOUND_HOWL,
	SOUND_FLUTTER,
	SOUND_ROAR,
	SOUND_PATTER,
	SOUND_SLITHER,
	WARNING_STINK,
	WARNING_SMOKE,
	WARNING_RUINS,
	WARNING_DANK,
	WARNING_BONES,
	SITE_STATUE,
	SITE_ALTAR,
	SITE_VAULT,
	SITE_POOL,
	SITE_HOARD,
	SITE_LAIR,
	SITE_CAIRNS,
	SITE_SHRINE,
	SITE_LOST_CITY,
	SITE_LOST_CASTLE,
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
