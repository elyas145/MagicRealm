package lwjglview.graphics.model;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collection;

import lwjglview.graphics.LWJGLGraphics;

import org.lwjgl.BufferUtils;

import utils.resources.ResourceHandler;
import view.graphics.Drawable;
import view.graphics.Graphics;

public class ModelData implements Drawable {
	
	public static ModelData loadModelData(ResourceHandler rh, String file)
			throws IOException {
		ModelData ret = new ModelData();
		String data = rh.readFile(ResourceHandler.joinPath("models", file));
		String[] split = data.split("\\r?\\n");
		for(String line: split) {
			ret.parseLine(line);
		}
		return ret;
	}

	private ModelData() {
		vertexSize = 0;
		vertices = new ArrayList<FloatBuffer>();
		texCoordSize = 0;
		textureCoordinates = new ArrayList<FloatBuffer>();
		normalSize = 0;
		normals = new ArrayList<FloatBuffer>();
		faces = new ArrayList<int[][]>();
		inGPU = false;
	}
	
	private void parseLine(String line) {
		String[] split = line.split(" +");
		if(split.length > 0 && split[0].length() > 0) {
			String nm = split[0];
			switch(nm.charAt(0)) {
			case 'v':
				switch(nm.length()) {
				case 1:
					parseVertex(split);
					break;
				case 2:
					switch(nm.charAt(2)) {
					case 't': // vt denotes texture coordinate
						parseTextureCoordinate(split);
						break;
					case 'n': // vn denotes vertex normal
						parseNormal(split);
						break;
					}
				}
			case 'f':
				parseFace(split);
				break;
			}
		}
	}

	@Override
	public void draw(Graphics gfx) {
		LWJGLGraphics lwgfx = (LWJGLGraphics) gfx;
		if(!inGPU) {
			uploadToGPU();
		}
	}
	
	private void parseVertex(String[] line) {
		if(vertexSize <= 0) {
			vertexSize = line.length - 1;
		}
		FloatBuffer fb = BufferUtils.createFloatBuffer(vertexSize);
		for(int i = 0; i < vertexSize; ++i) {
			fb.put(Float.parseFloat(line[i + 1]));
		}
		vertices.add(fb);
	}
	
	private void parseTextureCoordinate(String[] line) {
		if(texCoordSize <= 0) {
			texCoordSize = line.length - 1;
		}
		FloatBuffer fb = BufferUtils.createFloatBuffer(texCoordSize);
		for(int i = 0; i < texCoordSize; ++i) {
			fb.put(Float.parseFloat(line[i + 1]));
		}
		textureCoordinates.add(fb);
	}
	
	private void parseNormal(String[] line) {
		if(normalSize <= 0) {
			normalSize = line.length - 1;
		}
		FloatBuffer fb = BufferUtils.createFloatBuffer(normalSize);
		for(int i = 0; i < normalSize; ++i) {
			fb.put(Float.parseFloat(line[i + 1]));
		}
		normals.add(fb);
	}
	
	private void parseFace(String[] line) {
		int[][] face = new int[line.length - 1][];
		for(int i = 0; i + 1 < face.length; ++i) {
			face[i] = new int[3];
			String[] split = line[i].split("/");
			for(int j = 0; j < 3; ++j) {
				if(j < split.length) {
					face[i][j] = Integer.parseInt(split[j]);
				}
				else {
					face[i][j] = 0;
				}
			}
		}
		faces.add(face);
	}
	
	private void uploadToGPU() {
		System.out.println("TODO: Upload modeldata to gpu");
		inGPU = true;
	}
	
	private int vertexSize;
	private Collection<FloatBuffer> vertices;
	
	private int texCoordSize;
	private Collection<FloatBuffer> textureCoordinates;
	
	private int normalSize;
	private Collection<FloatBuffer> normals;
	
	private Collection<int[][]> faces;
	
	private boolean inGPU;
	private int verticesLocation;
	private int texCoordLocation;
	private int normalsLocation;

}
