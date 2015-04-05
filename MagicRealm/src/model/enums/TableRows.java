package model.enums;

import java.util.ArrayList;

public enum TableRows {
	CLUES_AND_PATHS,
	HIDDEN_ENEMIES_AND_PATHS,
	HIDDEN_ENEMIES,
	CLUES,
	NOTHING;
	
	public Iterable<TableRows> getPeer(){
		ArrayList<TableRows> tr = new ArrayList<TableRows>();
		tr.add(CLUES_AND_PATHS);
		tr.add(HIDDEN_ENEMIES_AND_PATHS);
		tr.add(HIDDEN_ENEMIES);
		tr.add(CLUES);
		tr.add(NOTHING);
		return tr;
	}	
	
	public String toString(){
		switch(this){
		case CLUES_AND_PATHS: return "Clues and Paths";
		case HIDDEN_ENEMIES_AND_PATHS: return "Hidden Enemies and Paths";
		case CLUES: return "Clues";
		case NOTHING: return "Nothing";
		default: return "none";
		}
	}
}
