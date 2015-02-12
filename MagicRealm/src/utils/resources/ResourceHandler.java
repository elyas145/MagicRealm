package utils.resources;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class ResourceHandler {
	
	// default constructor initializes maps
	public ResourceHandler() {
		files = new HashMap<String, String>();
		images = new HashMap<String, BufferedImage>();
	}
	
	public URL getResource(String fname) throws IOException {
		URL ret = getClass().getResource(ResourceHandler.joinPath(new String[]{
				"..", "..", "resources", fname
		}));
		if(ret == null) {
			throw new IOException("The file " + fname + " could not be found, "
					+ "try refreshing the project (F5)");
		}
		return ret;
	}
	
	public String readFile(String fname) throws IOException {
		URL ur = getResource(fname);
		String rep = ur.toString();
		if(!files.containsKey(rep)) {
			System.out.println("Reading file: " + rep);
			BufferedReader br;
      try {
        br = new BufferedReader(new FileReader(ur.toURI().getPath()));
      }
      catch(URISyntaxException e) {
        throw new RuntimeException(e);
      }
			char[] charbuf = new char[1024];
			int read = 0;
			StringBuilder sb = new StringBuilder();
			while((read = br.read(charbuf)) > 0) {
			  sb.append(charbuf, 0, read);
			}
			files.put(rep, sb.toString());
			br.close();
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
	
	public static String joinPath(String[] paths) {
		if(paths.length == 0) {
			throw new RuntimeException("The number of paths must not be 0");
		}
		if(paths.length == 1) {
			return paths[0];
		}
		ArrayList<String> parts = new ArrayList<String>();
		for(String p: paths) {
			parts.add(p);
		}
		return joinPath(parts.remove(0), parts);
	}
	
	private static String joinPath(String path, ArrayList<String> paths) {
		if(paths.isEmpty()) {
			return path;
		}
		return new File(path, joinPath(paths.remove(0), paths)).getPath();
	}
	
	private Map<String, String> files;
	private Map<String, BufferedImage> images;
	
}
