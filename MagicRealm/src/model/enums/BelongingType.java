package model.enums;

/**
 * TODO need to add different types of weapons and armor.
 * @author elyas
 *
 */
public enum BelongingType {
	HELMET, 
	BREAST_PLATE, 
	SHIELD,
	SHORT_SWORD, 
	THRUSTING_SWORD;
	
	public String toString(){
		switch(this){
		case HELMET: return "Helmet";
		case BREAST_PLATE: return "Breast Plate";
		case SHIELD: return "Shield";
		case SHORT_SWORD: return "Short Sword";
		case THRUSTING_SWORD: return "Thrusting Sword";
		default:
			return "None";
		}
	}
}
