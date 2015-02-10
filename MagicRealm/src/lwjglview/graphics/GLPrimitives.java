package lwjglview.graphics;

import static org.lwjgl.opengl.GL11.*;

public class GLPrimitives {
	
	public GLPrimitives() {
		hexagon = -1;
	}
	
	/*
	 * Makes a hexagon of width 2 centered at the origin
	 * 		2_3
	 * 	  1 /0\
	 *      \_/ 4
	 *      6 5
	 */
	public void drawHexagon() {
		if(hexagon < 0) {
			hexagon = glGenLists(1);
			glNewList(hexagon, GL_COMPILE);
			glBegin(GL_TRIANGLES);
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
			glEnd();
			glEndList();
		}
		else {
			glCallList(1);
		}
	}
	
	private void vertOrigin() {
		glTexCoord2f(.5f, .5f);
		glVertex3f(0f, 0f, 0f);
	}
	
	private void vertHex1() {
		glTexCoord2f(0f, .5f);
		glVertex3f(-1f, 0f, 0f);
	}
	
	private void vertHex2() {
		glTexCoord2f(0.25f, 0.9330127f);
		glVertex3f(-.5f, 0.866025f, 0f);
	}
	
	private void vertHex3() {
		glTexCoord2f(0.75f, 0.9330127f);
		glVertex3f(.5f, 0.866025f, 0f);
	}

	private void vertHex4() {
		glTexCoord2f(1f, .5f);
		glVertex3f(1f, 0f, 0f);
	}
	
	private void vertHex5() {
		glTexCoord2f(0.75f, 0.06698729f);
		glVertex3f(.5f, -0.866025f, 0f);
	}
	
	private void vertHex6() {
		glTexCoord2f(0.25f, 0.06698729f);
		glVertex3f(-.5f, -0.866025f, 0f);
	}
	
	private int hexagon;

}
