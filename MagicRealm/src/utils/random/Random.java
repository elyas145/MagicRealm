package utils.random;

import java.util.List;

public class Random {
	
	public static <T> T choose(List<T> lst) {
		return lst.get(randomInteger(lst.size()));
	}
	
	public static <T> T remove(List<T> lst) {
		return lst.remove(randomInteger(lst.size()));
	}
	
	// shuffles the list in place
	public static <T> void shuffle(List<T> lst) {
		for(int i = 0; i < lst.size(); ++i) {
			int swp = randomInteger(i, lst.size());
			T tmp = lst.get(i);
			lst.set(i, lst.get(swp));
			lst.set(swp, tmp);
		}
	}
	
	public static int dieRoll() {
		return randomInteger(1, 7);
	}

	public static int randomInteger(int size) {
		return randomInteger(0, size);
	}

	public static int randomInteger(int i, int size) {
		return (int) (random() * (size - i)) + i;
	}
	
	public static float random() {
		return (float) Math.random();
	}
	
	public static float random(float min, float max) {
		return (float) (Math.random() * (max - min) + min);
	}

}
