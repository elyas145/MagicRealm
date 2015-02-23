package utils.math;

public class Mathf {
	
	public static final float PI = (float) Math.PI;
	
	public static float sqr(float val) {
		return val * val;
	}
	
	public static float sqrt(float val) {
		return (float) Math.sqrt(val);
	}
	
	public static float length(float... vals) {
		float sm = 0f;
		for(float val: vals) {
			sm += val * val;
		}
		return sqrt(sm);
	}
	
	public static float sin(float ang) {
		return (float) Math.sin(ang);
	}

	public static float cos(float ang) {
		return (float) Math.cos(ang);
	}

}
