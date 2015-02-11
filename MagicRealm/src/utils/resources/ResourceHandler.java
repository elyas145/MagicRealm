package utils.resources;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class ResourceHandler {
	
	public ResourceHandler() {
		files = new HashMap<String, String>();
		images = new HashMap<String, BufferedImage>();
	}
	
	public URL getResource(String fname) throws IOException {
		return getClass().getResource(new File("/resources", fname).getPath());
	}
	
	public String readFile(String fname) throws IOException {
		URL ur = getResource(fname);
		String rep = ur.toString();
		if(!files.containsKey(rep)) {
			System.out.println("Reading file: " + rep);
			files.put(rep, new String(Files.readAllBytes(Paths.get(ur.getFile()))));
		}
		String loaded = files.get(rep);
		System.out.println("Retreived file: " + rep);
		System.out.println(loaded);
		return loaded;
	}
	
	public BufferedImage readImage(String fname) throws IOException {
		URL ur = getResource(fname);
		String rep = ur.toString();
		if(!images.containsKey(rep)) {
			System.out.println("Reading image: " + rep);
			images.put(rep, ImageIO.read(ur));
		}
		System.out.println("Retreived image: " + rep);
		BufferedImage bi = images.get(rep);
		System.out.println("Dimensions: " + bi.getWidth() + ", " + bi.getHeight());
		return bi;
	}
	
	private Map<String, String> files;
	private Map<String, BufferedImage> images;
	
}
