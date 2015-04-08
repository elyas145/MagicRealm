package config;

import model.enums.CounterType;

public class GameConfiguration {
	public static final int NUMBER_OF_TREASURES = 5;
	public static final int MAX_PLAYERS = 3;
	public static final int LUNAR_MONTH = 28;
	public static final int INITIAL_PHASES = 2;
	public static final int SUNLIGHT_PHASES = 2;

	public static final CounterType INITIAL_SITE = CounterType.VALLEY_INN;
	
	public static boolean Cheat = false;
	public static boolean RANDOM = false;
	public static final boolean DEBUG_MODE = true;
	public static final int MAX_TREASURE_VALUE = 50;
}