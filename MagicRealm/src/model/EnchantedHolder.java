package model;

public class EnchantedHolder <T> {
	
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
	
	private T normal;
	private T enchanted;
}
