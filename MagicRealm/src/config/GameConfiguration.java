package config;

import model.enums.ValleyChit;

public class GameConfiguration {
	public static final int NUMBER_OF_TREASURES = 5;
	public static final int MAX_PLAYERS = 2;
	public static final int LUNAR_MONTH = 28;
	public static final int INITIAL_PHASES = 2;
	public static final int SUNLIGHT_PHASES = 2;

	public static final ValleyChit INITIAL_SITE = ValleyChit.INN;
	
	public static boolean Cheat = false;
	public static final boolean DEBUG_MODE = true;
}