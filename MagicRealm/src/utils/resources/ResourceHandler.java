package utils.resources;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

public class ResourceHandler {
	
	public URL getResource(String fname) throws IOException {
		File fl = new File("/res", fname);
		System.out.println(fl.getPath());
		return getClass().getResource(fl.getPath());
	}
	
	public String readFile(String fname) throws IOException {
		return new String(Files.readAllBytes(Paths.get(getResource(fname).getFile())));
	}
	
	public BufferedImage readImage(String fname) throws IOException {
		URL ur = getResource(fname);
		System.out.println(ur);
		return ImageIO.read(ur);
	}
	
}
