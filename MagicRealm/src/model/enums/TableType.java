
package model.enums;

import java.util.ArrayList;

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

	public void getSearchTypes(ArrayList<SearchType> types) {
		switch(this) {
		case PEER:
			types.add(SearchType.CHOICE);
			types.add(SearchType.CLUES_PATHS);
			types.add(SearchType.CLUES);
			types.add(SearchType.NONE);
			break;
		case LOCATE:
			types.add(SearchType.CHOICE);
			types.add(SearchType.PASSAGES_CLUES);
			types.add(SearchType.PASSAGES);
			types.add(SearchType.DISCOVER_CHITS);
			types.add(SearchType.NONE);
			break;
		case LOOT:
			break;
		}
	}

	public int getRollValue(SearchType st) {
		switch(this) {
		case PEER:
			switch(st) {
			case CHOICE:
				return 1;
			case CLUES_PATHS:
				return 2;
			case CLUES:
				return 5;
			default:
				break;
			}
			break;
		case LOCATE:
			switch(st) {
			case CHOICE:
				return 1;
			case PASSAGES_CLUES:
				return 2;
			case PASSAGES:
				return 3;
			case DISCOVER_CHITS:
				return 4;
			default:
				break;
			}
			break;
		default:
			break;
		}
		return 6;
	}
}
