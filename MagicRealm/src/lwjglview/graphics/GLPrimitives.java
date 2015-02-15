package lwjglview.graphics;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

public class GLPrimitives {
	
	public GLPrimitives(LWJGLGraphics drawer) {
		hexagon = -1;
		graphics = drawer;
		vertBuf = BufferUtils.createFloatBuffer(3);
		texBuf = BufferUtils.createFloatBuffer(2);
	}
	
	/*
	 * Makes a hexagon of width 2 centered at the origin
	 * 		  2_3
	 * 	  1 /0\
	 *      \_/ 4
	 *      6 5
	 */
	public void drawHexagon() {
		if(hexagon < 0) {
			hexagon = graphics.startTriangleList();
			// triangle 0 1 2
				// vertex 0
				vertOrigin();
				// vertex 1
				vertHex1();
				// vertex 2
				vertHex2();
			// triangle 0 2 3
				// vertex 0
				vertOrigin();
				// vertex 2
				vertHex2();
				// vertex 3
				vertHex3();
			// triangle 0 3 4
				// vertex 0
				vertOrigin();
				// vertex 3
				vertHex3();
				// vertex 4
				vertHex4();
			// triangle 0 4 5
				// vertex 0
				vertOrigin();
				// vertex 3
				vertHex4();
				// vertex 4
				vertHex5();
			// triangle 0 5 6
				// vertex 0
				vertOrigin();
				// vertex 5
				vertHex5();
				// vertex 6
				vertHex6();
			// triangle 0 6 1
				// vertex 0
				vertOrigin();
				// vertex 6
				vertHex6();
				// vertex 1
				vertHex1();
			graphics.endList();
		}
		else {
			graphics.callList(hexagon);
		}
	}
	
	private void vertex(float a, float b, float c) {
		vertBuf.put(0, a);
		vertBuf.put(1, b);
		vertBuf.put(2, c);
		graphics.setVertex(3, vertBuf);
	}
	
	private void texCoord(float a, float b) {
		texBuf.put(0, a);
		texBuf.put(1, b);
		graphics.setTextureCoordinate(2, texBuf);
	}
	
	private void vertOrigin() {
		texCoord(.5f, .5f);
		vertex(0f, 0f, 0f);
	}
	
	private void vertHex1() {
		texCoord(0f, .5f);
		vertex(-1f, 0f, 0f);
	}
	
	private void vertHex2() {
		texCoord(0.25f, 0f);
		vertex(-.5f, 0.866025f, 0f);
	}
	
	private void vertHex3() {
		texCoord(0.75f, 0f);
		vertex(.5f, 0.866025f, 0f);
	}

	private void vertHex4() {
		texCoord(1f, .5f);
		vertex(1f, 0f, 0f);
	}
	
	private void vertHex5() {
		texCoord(0.75f, 1f);
		vertex(.5f, -0.866025f, 0f);
	}
	
	private void vertHex6() {
		texCoord(0.25f, 1f);
		vertex(-.5f, -0.866025f, 0f);
	}
	
	private int hexagon;
	private LWJGLGraphics graphics;
	private FloatBuffer vertBuf;
	private FloatBuffer texBuf;

}
