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
	GUEARD_HOUSE,
	HOUSE,
	INN;
	
	public CounterType toCounterType(){
		switch(this){
		case STATUE:
			return CounterType.SITE_STATUE;
		case INN:
			return CounterType.SITE_INN;
		}
		return CounterType.NONE;
	}
}
