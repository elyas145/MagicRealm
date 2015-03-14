package model.character.belonging;

import model.enums.WeaponType;
import model.interfaces.BelongingType;

public class Weapon extends Belonging {
	private boolean alerted;
	private int attackTime;
	private WeaponType weaponType;
	public WeaponType getWeaponType() {
		return weaponType;
	}

	public void setWeaponType(WeaponType weaponType) {
		this.weaponType = weaponType;
		setType(weaponType.toBelonging());
	}

	private char harm;

	private int sharpness;

	public Weapon(WeaponType type, int sharpness, boolean alerted,
			int attackTime, char harm) {
		this.harm = harm;
		this.sharpness = sharpness;
		this.alerted = alerted;
		this.attackTime = attackTime;
		this.weaponType = type;

	}

	public int getAttackTime() {
		return attackTime;
	}

	public char getHarm() {
		return harm;
	}

	public int getSharpness() {
		return sharpness;
	}

	public boolean isAlerted() {
		return alerted;
	}

	public void setAlerted(boolean alerted) {
		this.alerted = alerted;
	}

	public void setAttackTime(int attackTime) {
		this.attackTime = attackTime;
	}

	public void setHarm(char harm) {
		this.harm = harm;
	}

	public void setSharpness(int sharpness) {
		this.sharpness = sharpness;
	}
}
