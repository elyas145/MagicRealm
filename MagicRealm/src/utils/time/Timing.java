package utils.time;

public class Timing {
	
	// accurate to ms, period of 10000s
	public static float getSeconds() {
		long nanotime = System.nanoTime() % 1000000000000000L;
		int ms = (int) (nanotime / 1000000);
		return ms * 1e-3f;
	}
	
	// accurate to us, period of 10s
	public static float getMilliSeconds() {
		long nanotime = System.nanoTime() % 1000000000000L;
		int us = (int) (nanotime / 1000);
		return us * 1e-3f;
	}

}
