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
		case SITE_CHAPEL:
		case SITE_GUARD_HOUSE:
		case SITE_HOUSE:
		case SITE_HUT:
		case SITE_INN:
		case SITE_LARGE_FIRE:
		case SITE_SMALL_FIRE:
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
		case SITE_CHAPEL:
			return "chapel.gif";
		case SITE_GUARD_HOUSE:
			return "guard.gif";
		case SITE_HOUSE:
			return "house.gif";
		case SITE_HUT:
			return "hut.gif";
		case SITE_INN:
			return "inn.gif";
		case SITE_LARGE_FIRE:
			return "large_fire.gif";
		case SITE_SMALL_FIRE:
			return "small_fire.gif";
		default:
			return "penguin.png";
		}
	}

}
