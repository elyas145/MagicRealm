package model.enums;

public enum ValleyChit {
	CHAPEL,
	GUARD_HOUSE,
	HOUSE,
	INN;
	
	public CounterType toCounterType(){
		switch(this){
		case CHAPEL:
			return CounterType.SITE_CHAPEL;
		case GUARD_HOUSE:
			return CounterType.SITE_GUARD_HOUSE;
		case HOUSE:
			return CounterType.SITE_HOUSE;
		case INN:
			return CounterType.SITE_INN;
		default:
			return CounterType.NONE;
		}
	}
}


