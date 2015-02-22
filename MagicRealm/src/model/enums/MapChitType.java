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
	
	public static final MapChitType[] SOUNDS = {
		HOWL,
		FLUTTER,
		ROAR,
		PATTER,
		SLITHER
	};
	
	public static final MapChitType[] WARNINGS = {
		STINK,
		SMOKE,
		RUINS,
		DANK,
		BONES
	};
	
	public static final MapChitType[] SITES = {
		STATUE,
		ALTAR,
		VAULT,
		POOL,
		HOARD,
		LAIR,
		CAIRNS,
		SHRINE,
		LOST_CITY,
		LOST_CASTLE
	};
	
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

	public CounterType toCounter() {
		switch(this) {
		case HOWL:
			return CounterType.SOUND_HOWL;
		case FLUTTER:
			return CounterType.SOUND_FLUTTER;
		case ROAR:
			return CounterType.SOUND_ROAR;
		case PATTER:
			return CounterType.SOUND_PATTER;
		case SLITHER:
			return CounterType.SOUND_SLITHER;
		case STINK:
			return CounterType.WARNING_STINK;
		case SMOKE:
			return CounterType.WARNING_SMOKE;
		case RUINS:
			return CounterType.WARNING_RUINS;
		case DANK:
			return CounterType.WARNING_DANK;
		case BONES:
			return CounterType.WARNING_BONES;
		case STATUE:
			return CounterType.SITE_STATUE;
		case ALTAR:
			return CounterType.SITE_ALTAR;
		case VAULT:
			return CounterType.SITE_VAULT;
		case POOL:
			return CounterType.SITE_POOL;
		case HOARD:
			return CounterType.SITE_HOARD;
		case LAIR:
			return CounterType.SITE_LAIR;
		case CAIRNS:
			return CounterType.SITE_CAIRNS;
		case SHRINE:
			return CounterType.SITE_SHRINE;
		case LOST_CITY:
			return CounterType.SITE_LOST_CITY;
		case LOST_CASTLE:
			return CounterType.SITE_LOST_CASTLE;
		default:
			return CounterType.NONE;
		}
	}
}
