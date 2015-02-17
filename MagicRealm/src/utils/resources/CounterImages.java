package utils.resources;

import java.awt.image.BufferedImage;
import java.io.IOException;

import model.enums.CounterType;

public class CounterImages {

	public static BufferedImage getCounterImage(ResourceHandler rh, CounterType type)
			throws IOException {
		return rh.readImage(ResourceHandler.joinPath("images", "counters",
				getSubDir(type), getName(type) + ".png"));
	}

	private static String getSubDir(CounterType tile) {
		switch (tile) {
		case CHARACTER_AMAZON:
		case CHARACTER_CAPTAIN:
		case CHARACTER_SWORDSMAN:
			return "characters";
		default:
			return ".";
		}
	}

	private static String getName(CounterType tile) {
		switch (tile) {
		case CHARACTER_AMAZON:
			return "amazon";
		case CHARACTER_CAPTAIN:
			return "captain";
		case CHARACTER_SWORDSMAN:
			return "swordsman";
		default:
			return "penguin";
		}
	}

}
