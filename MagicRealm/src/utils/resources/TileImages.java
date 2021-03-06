package utils.resources;

import java.awt.image.BufferedImage;
import java.io.IOException;

import model.enums.TileName;

public class TileImages {

	public static BufferedImage getTileImage(ResourceHandler rh, TileName type,
			boolean enchanted) throws IOException {
		return Images.getScaledImage(rh, ResourceHandler.joinPath("tiles",
			getName(type) + (enchanted ? "-e1.gif" : "1.gif")));
	}

	private static String getName(TileName tile) {
		switch (tile) {
		case AWFUL_VALLEY:
			return "awfulvalley";
		case BAD_VALLEY:
			return "badvalley";
		case BORDERLAND:
			return "borderland";
		case CAVERN:
			return "cavern";
		case CAVES:
			return "caves";
		case CLIFF:
			return "cliff";
		case CRAG:
			return "crag";
		case CURST_VALLEY:
			return "curstvalley";
		case DARK_VALLEY:
			return "darkvalley";
		case DEEP_WOODS:
			return "deepwoods";
		case EVIL_VALLEY:
			return "evilvalley";
		case HIGH_PASS:
			return "highpass";
		case LEDGES:
			return "ledges";
		case LINDEN_WOODS:
			return "lindenwoods";
		case MAPLE_WOODS:
			return "maplewoods";
		case MOUNTAIN:
			return "mountain";
		case NUT_WOODS:
			return "nutwoods";
		case OAK_WOODS:
			return "oakwoods";
		case PINE_WOODS:
			return "pinewoods";
		case RUINS:
			return "ruins";
		default:
			return "colourful";
		}
	}

}
