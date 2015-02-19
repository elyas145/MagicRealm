package model.enums;

public enum SiteType {
	//STATUE,
	//ALTAR,
	//VAULT,
	//POOL,
	//LOST_CITY,
	//HOARD,
	//LAIR,
	//CAIRNS,
	//SHRINE,
	//LOST_CASTLE,
	CHAPEL,
	GUARD_HOUSE,
	HOUSE,
	INN;
	
	public CounterType toCounterType(){
		switch(this){
		case CHAPEL:
			return CounterType.SITE_CHAPEL;
		case INN:
			return CounterType.SITE_INN;
		case GUARD_HOUSE:
			return CounterType.SITE_GUARD_HOUSE;
		case HOUSE:
			return CounterType.SITE_HOUSE;
		}
		return CounterType.NONE;
	}
}
