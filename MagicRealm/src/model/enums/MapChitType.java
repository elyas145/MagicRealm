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
		SHRINE
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
	
	public int getClearing() {
		switch(this) {
		case HOWL:
			return 4;
		case FLUTTER:
			return 1;
		case ROAR:
			return 6;
		case PATTER:
			return 2;
		case SLITHER:
			return 3;
		case STATUE:
			return 2;
		case ALTAR:
			return 1;
		case VAULT:
			return 3;
		case POOL:
			return 6;
		case HOARD:
			return 6;
		case LAIR:
			return 3;
		case CAIRNS:
			return 5;
		case SHRINE:
			return 4;
		case LOST_CITY:
			return 3;
		case LOST_CASTLE:
			return 1;
		default:
			return 0;
		}
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
	
	public String toString(){
		switch (this){
		case HOWL: return "Howl";
		case FLUTTER: return "Flutter";
		case ROAR: return "Roar";
		case PATTER: return "Patter";
		case SLITHER: return "Slither";
		case STINK: return "Stink";
		case SMOKE: return "Smoke";
		case RUINS: return "Ruins";
		case DANK: return "Dank";
		case BONES: return "Bones";
		case STATUE: return "Statue";
		case ALTAR: return "Altar";
		case VAULT: return "Vault";
		case POOL: return "Pool";
		case HOARD: return "Hoard";
		case LAIR: return "Lair";
		case CAIRNS: return "Cairns";
		case SHRINE: return "Shrine";
		case LOST_CITY: return "Lost City";
		case LOST_CASTLE: return "Lost Castle";
		default:
			return "None";
		}
	}
}
