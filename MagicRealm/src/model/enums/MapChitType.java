package model.enums;

public enum MapChitType {
	// sound
	HOWL,
	FLUTTER,
	ROAR,
	PATTER,
	SLITHER,
	
	// warning
	STINK,
	SMOKE,
	RUINS,
	DANK,
	BONES,
	
	// site
	STATUE,
	ALTAR,
	VAULT,
	POOL,
	HOARD,
	LAIR,
	CAIRNS,
	SHRINE,
	
	//lost
	LOST_CITY,
	LOST_CASTLE;
	
	public ChitType type(){
		switch (this){
		case HOWL:
		case FLUTTER:
		case ROAR:
		case PATTER:
		case SLITHER:
			return ChitType.SOUND;
		case STINK:
		case SMOKE:
		case RUINS:
		case DANK:
		case BONES:
			return ChitType.WARNING;
		case STATUE:
		case ALTAR:
		case VAULT:
		case POOL:
		case HOARD:
		case LAIR:
		case CAIRNS:
		case SHRINE:
			return ChitType.SITE;
		case LOST_CITY:
			return ChitType.LOST_CITY;
		case LOST_CASTLE:
			return ChitType.LOST_CASTLE;
		}
		return null;
	}
}
