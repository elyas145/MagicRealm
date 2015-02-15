package utils.images;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

public class ImageTools {

	public static int loadRawImage(BufferedImage img, int offset, int width, int height,
			ByteBuffer dest) {
		for (int y = 0; y < height; ++y) {
			for (int x = 0; x < width; ++x) {
				int i = img.getRGB(x, y);
				dest.putInt(offset, i);
				offset += 4;
			}
		}
		return offset;
	}

	public static int loadRawImageArray(BufferedImage[] imgs, int offset, int width,
			int height, ByteBuffer dest) {
		for (BufferedImage img : imgs) {
			offset = loadRawImage(img, offset, width, height, dest);
		}
		return offset;
	}

}
