package utils.resources;

import java.awt.image.BufferedImage;
import java.io.IOException;

import model.enums.ChitType;
import model.enums.TileType;

public class ChitImages {

	public static BufferedImage getChitImage(ResourceHandler rh, ChitType type)
			throws IOException {
		return rh.readImage(ResourceHandler.joinPath("images", "chits",
				getSubDir(type), getName(type) + ".png"));
	}

	private static String getSubDir(ChitType tile) {
		switch (tile) {
		case CHARACTER_AMAZON:
		case CHARACTER_CAPTAIN:
		case CHARACTER_SWORDSMAN:
			return "characters";
		default:
			return ".";
		}
	}

	private static String getName(ChitType tile) {
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
