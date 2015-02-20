package model.counter.chit;

import model.enums.TileName;
import model.enums.WarningType;

public class WarningChit extends Chit {
	char identifier;
	WarningType type;
	public WarningType getType() {
		return type;
	}
	public void setType(WarningType type) {
		this.type = type;
	}
	public char getIdentifier() {
		return identifier;
	}
	public void setIdentifier(char identifier) {
		this.identifier = identifier;
	}
	
	public WarningChit(TileName tt, char identifier, WarningType type) {
		super(tt);
		this.identifier = identifier;
		this.type = type;
	}
	
	public WarningChit(char identifier, WarningType type) {
		this.identifier = identifier;
		this.type = type;
	}
	
}
