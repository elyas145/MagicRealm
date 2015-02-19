package model.enums;

public enum SiteType {
	STATUE,
	ALTAR,
	VAULT,
	POOL,
	LOST_CITY,
	HOARD,
	LAIR,
	CAIRNS,
	SHRINE,
	LOST_CASTLE,
	CHAPEL,
	GUARD_HOUSE,
	HOUSE,
	HUT,
	INN,
	LARGE_FIRE,
	SMALL_FIRE;
	
	public CounterType toCounterType(){
		switch(this){
		case STATUE:
			return CounterType.SITE_STATUE;
		case ALTAR:
			return CounterType.SITE_ALTAR;
		case VAULT:
			return CounterType.SITE_VAULT;
		case POOL:
			return CounterType.SITE_POOL;
		case LOST_CITY:
			return CounterType.SITE_LOST_CITY;
		case HOARD:
			return CounterType.SITE_HOARD;
		case LAIR:
			return CounterType.SITE_LAIR;
		case CAIRNS:
			return CounterType.SITE_CAIRNS;
		case SHRINE:
			return CounterType.SITE_SHRINE;
		case LOST_CASTLE:
			return CounterType.SITE_LOST_CASTLE;
		case CHAPEL:
			return CounterType.SITE_CHAPEL;
		case GUARD_HOUSE:
			return CounterType.SITE_GUARD_HOUSE;
		case HOUSE:
			return CounterType.SITE_HOUSE;
		case HUT:
			return CounterType.SITE_HUT;
		case INN:
			return CounterType.SITE_INN;
		case LARGE_FIRE:
			return CounterType.SITE_LARGE_FIRE;
		case SMALL_FIRE:
			return CounterType.SITE_SMALL_FIRE;
		default:
			return CounterType.NONE;
		}
	}
}
