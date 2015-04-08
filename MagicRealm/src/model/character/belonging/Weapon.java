package model.character.belonging;

import model.enums.BelongingType;

public class Weapon extends Belonging {
	
	private boolean alerted;
	private int attackTime;

	private char harm;

	private int sharpness;

	public Weapon(BelongingType type, int sharpness, boolean alerted,
			int attackTime, char harm) {
		super(null, alerted, type);
		this.harm = harm;
		this.sharpness = sharpness;
		this.alerted = alerted;
		this.attackTime = attackTime;

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
	
	private static final long serialVersionUID = -1253148762099824573L;
	
}
