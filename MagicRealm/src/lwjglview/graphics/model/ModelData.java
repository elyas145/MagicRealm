package lwjglview.graphics.model;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lwjglview.graphics.LWJGLDrawable;
import lwjglview.graphics.LWJGLDrawableNode;
import lwjglview.graphics.LWJGLGraphics;

import org.lwjgl.BufferUtils;

import utils.resources.ResourceHandler;
import view.graphics.Drawable;
import view.graphics.Graphics;

public class ModelData extends LWJGLDrawable {

	public static ModelData loadModelData(ResourceHandler rh, String file)
			throws IOException {
		ModelData ret = new ModelData();
		String data = rh.readFile(ResourceHandler.joinPath("models", file));
		String[] split = data.split("\\r?\\n");
		for (String line : split) {
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
		if (split.length > 0 && split[0].length() > 0) {
			String nm = split[0];
			char f = nm.charAt(0);
			switch (nm.length()) {
			case 1:
				switch (f) {
				case 'v':
					parseVertex(split);
					break;
				case 'f':
					parseFace(split);
					break;
				}
				break;
			case 2:
				if (f == 'v') {
					switch (nm.charAt(1)) {
					case 't': // vt denotes texture coordinate
						parseTextureCoordinate(split);
						break;
					case 'n': // vn denotes vertex normal
						parseNormal(split);
						break;
					}
				}
				break;
			}
		}
	}

	@Override
	public void updateUniforms(LWJGLGraphics gfx) {
	}

	@Override
	public void draw(LWJGLGraphics lwgfx) {
		if (!inGPU) {
			uploadToGPU(lwgfx);
		} else {
			lwgfx.callList(verticesLocation);
		}
	}

	@Override
	public void draw(Graphics gfx) {
		LWJGLGraphics lwgfx = (LWJGLGraphics) gfx;
		draw(lwgfx);
	}

	private void parseVertex(String[] line) {
		if (vertexSize <= 0) {
			vertexSize = line.length - 1;
		}
		FloatBuffer fb = BufferUtils.createFloatBuffer(vertexSize);
		for (int i = 0; i < vertexSize; ++i) {
			fb.put(Float.parseFloat(line[i + 1]));
		}
		vertices.add(fb);
	}

	private void parseTextureCoordinate(String[] line) {
		if (texCoordSize <= 0) {
			texCoordSize = line.length - 1;
		}
		FloatBuffer fb = BufferUtils.createFloatBuffer(texCoordSize);
		for (int i = 0; i < texCoordSize; ++i) {
			fb.put(Float.parseFloat(line[i + 1]));
		}
		textureCoordinates.add(fb);
	}

	private void parseNormal(String[] line) {
		if (normalSize <= 0) {
			normalSize = line.length - 1;
		}
		FloatBuffer fb = BufferUtils.createFloatBuffer(normalSize);
		for (int i = 0; i < normalSize; ++i) {
			fb.put(Float.parseFloat(line[i + 1]));
		}
		normals.add(fb);
	}

	private void parseFace(String[] line) {
		// face is array of vertex indices
		int[][] face = new int[line.length - 1][];
		for (int i = 0; i < face.length; ++i) {
			// vertex is v/vt/vn
			face[i] = new int[3];
			String[] split = line[i + 1].split("/");
			for (int j = 0; j < 3; ++j) {
				if (j < split.length && split[j].length() > 0) {
					face[i][j] = Integer.parseInt(split[j]);
				} else {
					face[i][j] = 0;
				}
			}
		}
		faces.add(face);
	}

	private void uploadToGPU(LWJGLGraphics gfx) {
		verticesLocation = gfx.startList();
		char cur = 'n';
		int len = 0;
		for (int[][] face : faces) {
			if(face.length == 3) { // triangle face
				if(cur != 't') {
					if(cur != 'n') {
						gfx.endPrimitives();
					}
					cur = 't';
					len = 3;
					gfx.startTriangles();
				}
			}
			else if(face.length == 4) { // quad face
				if(cur != 'q') {
					if(cur != 'n') {
						gfx.endPrimitives();
					}
					cur = 'q';
					len = 4;
					gfx.startQuads();
				}
			}
			else { // invalid face
				gfx.endList();
				System.out.println("Ignoring bad model data: " + face.length);
			}
			for (int i = 0; i < len; ++i) {
				FloatBuffer vert = vertices.get(face[i][0] - 1);
				int texLoc = face[i][1];
				FloatBuffer texCoord;
				int texSZ = texCoordSize;
				if (texLoc > 0 && texSZ > 0) {
					texCoord = textureCoordinates.get(texLoc - 1);
				} else {
					texSZ = vertexSize;
					texCoord = BufferUtils.createFloatBuffer(vertexSize);
					for (int j = 0; j < vertexSize; ++j) {
						// move [-1, 1] to [0, 1]
						// flip y
						if (j == 1) {
							texCoord.put(j, 1f - (vert.get(j) + 1f) * .5f);
						} else {
							texCoord.put(j, (vert.get(j) + 1f) * .5f);
						}
					}
				}
				gfx.setTextureCoordinate(texSZ, texCoord);
				int normLoc = face[i][2];
				if (normLoc > 0) {
					gfx.setNormal(normalSize, normals.get(normLoc - 1));
				}
				gfx.setVertex(vertexSize, vert);
			}
		}
		gfx.endList();
		inGPU = true;
	}

	private int vertexSize;
	private List<FloatBuffer> vertices;

	private int texCoordSize;
	private List<FloatBuffer> textureCoordinates;

	private int normalSize;
	private List<FloatBuffer> normals;

	private Collection<int[][]> faces;

	private boolean inGPU;
	private int verticesLocation;

}
