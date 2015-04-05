package model.enums;

public enum PhaseType {
	DEFAULT,
	SPECIAL,
	SUNLIGHT;
	
	public String toString(){
		switch(this){
		case DEFAULT: return "Default";
		case SPECIAL: return "Special";
		case SUNLIGHT: return "Sun Light";
		}
		return "None";
	}
}
