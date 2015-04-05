
package model.enums;

public enum TableType {
	PEER,
	LOCATE,
	LOOT;
	
	public String toString(){
		switch(this){
		case PEER: return "Peer";
		case LOCATE: return "Locate";
		case LOOT: return "Loot";
		default: return "None";
		}
	}
}
