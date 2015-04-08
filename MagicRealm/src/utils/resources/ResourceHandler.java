package utils.resources;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import config.GameConfiguration;

public class ResourceHandler {

	// default constructor initializes maps
	public ResourceHandler() {
		files = new HashMap<String, String>();
		images = new HashMap<String, BufferedImage>();
		counterGenerator = new LWJGLCounterGenerator(this);
	}
	
	public LWJGLCounterGenerator getCounterGenerator() {
		return counterGenerator;
	}

	public String getResource(String fname) throws IOException {
		if (GameConfiguration.DEBUG_MODE) {
			String path = ResourceHandler.class.getProtectionDomain()
					.getCodeSource().getLocation().getPath();
			return ResourceHandler.joinPath(path, "resources", fname);
		} else {
			return ResourceHandler.joinPath(path(), "resources", fname);
		}
	}

	public String readFile(String fname) throws IOException {
		String ur = getResource(fname);
		// System.out.println("Reading file: " + rep);
		if (!files.containsKey(ur)) {
			BufferedReader br;
			br = new BufferedReader(new FileReader(ur));
			char[] charbuf = new char[1024];
			int read = 0;
			StringBuilder sb = new StringBuilder();
			while ((read = br.read(charbuf)) > 0) {
				sb.append(charbuf, 0, read);
			}
			files.put(ur, sb.toString());
			br.close();
		}
		String loaded = files.get(ur);
		// System.out.println("Retreived file: " + rep);
		// System.out.println(loaded);
		return loaded;
	}

	public BufferedImage readImage(String fname) throws IOException {
		String ur = getResource(fname);
		String rep = ur.toString();
		System.out.println("HELOOOO: " + rep);
		if (!images.containsKey(rep)) {
			images.put(rep, ImageIO.read(new File(ur)));
		}
		BufferedImage bi = images.get(rep);
		return bi;
	}

	public static String joinPath(String... paths) {
		if (paths.length == 0) {
			throw new RuntimeException("The number of paths must not be 0");
		}
		if (paths.length == 1) {
			return paths[0];
		}
		ArrayList<String> parts = new ArrayList<String>();
		for (String p : paths) {
			parts.add(p);
		}
		return joinPath(parts.remove(0), parts);
	}

	private static String joinPath(String path, ArrayList<String> paths) {
		if (paths.isEmpty()) {
			return path;
		}
		return new File(path, joinPath(paths.remove(0), paths)).toString();
	}

	private String path() throws UnsupportedEncodingException {
		URL url1 = getClass().getResource("");
		String ur = url1.toString();
		ur = ur.substring(9);
		String truePath = ur.split("client.jar")[0];
		truePath = URLDecoder.decode(truePath, "UTF-8");
		return truePath;
	}

	private Map<String, String> files;
	private Map<String, BufferedImage> images;
	private LWJGLCounterGenerator counterGenerator;

}
