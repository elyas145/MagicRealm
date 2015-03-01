package model;

import java.io.Serializable;

public class EnchantedHolder <T> implements Serializable {
	private static final long serialVersionUID = 3008920658203964520L;
	public EnchantedHolder() {
		normal = enchanted = null;
	}
	
	public EnchantedHolder(T norm, T ench) {
		normal = norm;
		enchanted = ench;
	}
	
	public T get(boolean ench) {
		return ench ? enchanted : normal; 
	}
	
	public void set(boolean ench, T val) {
		if(ench) {
			enchanted = val;
		}
		else {
			normal = val;
		}
	}
	
	public boolean has(boolean ench) {
		return ench ? enchanted != null : normal != null;
	}
	
	@Override
	public String toString() {
		return "Normal: " + normal + ", Enchanted: " + enchanted;
	}
	
	private T normal;
	private T enchanted;
}
