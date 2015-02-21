package utils.resources;

import java.awt.image.BufferedImage;
import java.io.IOException;

import model.enums.CounterType;

public class CounterImages {

	public static BufferedImage getCounterImage(ResourceHandler rh,
			CounterType type) throws IOException {
		return Images.getImage(rh, ResourceHandler.joinPath("counters", getSubDir(type), getName(type)));
	}

	private static String getSubDir(CounterType tile) {
		switch (tile) {
		case CHARACTER_AMAZON:
		case CHARACTER_CAPTAIN:
		case CHARACTER_SWORDSMAN:
			return "characters";
		case VALLEY_CHAPEL:
		case VALLEY_GUARD_HOUSE:
		case VALLEY_HOUSE:
		case VALLEY_INN:
			return "sites";
		default:
			return ".";
		}
	}

	private static String getName(CounterType tile) {
		switch (tile) {
		case CHARACTER_AMAZON:
			return "amazon.png";
		case CHARACTER_CAPTAIN:
			return "captain.png";
		case CHARACTER_SWORDSMAN:
			return "swordsman.png";
		case VALLEY_CHAPEL:
			return "chapel.gif";
		case VALLEY_GUARD_HOUSE:
			return "guard.gif";
		case VALLEY_HOUSE:
			return "house.gif";
		case VALLEY_INN:
			return "inn.gif";
		default:
			return "penguin.png";
		}
	}

}
