package model.enums;

import model.interfaces.BelongingType;

public enum WeaponType {
	SHORT_SWORD, THRUSTING_SWORD;

	public BelongingType toBelonging() {
		switch(this){
		case SHORT_SWORD:
			return BelongingType.WEAPON;
		}
		return null;
	}
}
