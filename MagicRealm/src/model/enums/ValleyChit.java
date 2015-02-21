package model.enums;

public enum ValleyChit {
	CHAPEL,
	GUARD_HOUSE,
	HOUSE,
	INN;
	
	public CounterType toCounterType(){
		switch(this){
		case CHAPEL:
			return CounterType.VALLEY_CHAPEL;
		case GUARD_HOUSE:
			return CounterType.VALLEY_GUARD_HOUSE;
		case HOUSE:
			return CounterType.VALLEY_HOUSE;
		case INN:
			return CounterType.VALLEY_INN;
		default:
			return CounterType.NONE;
		}
	}
}


