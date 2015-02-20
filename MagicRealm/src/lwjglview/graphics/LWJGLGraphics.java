package lwjglview.graphics;

import lwjglview.graphics.board.LWJGLBoardDrawable;
import lwjglview.graphics.shader.GLShaderHandler;
import lwjglview.graphics.shader.ShaderType;
import model.board.Board;
import model.enums.TileName;
import model.interfaces.HexTileInterface;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import config.GraphicsConfiguration;
import controller.ControllerMain;
import utils.math.Mathf;
import utils.math.Matrix;
import utils.resources.ResourceHandler;
import view.graphics.Drawable;
import view.graphics.Graphics;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.ARBTextureStorage.*;
import static org.lwjgl.system.MemoryUtil.*;

public final class LWJGLGraphics implements Graphics {

	public static void main(String[] args) throws IOException {
		ResourceHandler rh = new ResourceHandler();
		LWJGLGraphics gfx = new LWJGLGraphics(rh, null);
		LWJGLBoardDrawable db = new LWJGLBoardDrawable(rh);
		gfx.addDrawable(db);
		gfx.start();
		Board b = new Board(rh);
		for (HexTileInterface hti : b.iterateTiles()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			db.setTile(hti.getName(), hti.getBoardColumn(), hti.getBoardRow(),
					hti.getRotation(), hti.getClearings());
		}
	}

	public LWJGLGraphics(ResourceHandler rh) {
		init(rh);
		control = null;
	}

	public LWJGLGraphics(ResourceHandler rh, ControllerMain cm) {
		init(rh);
		control = cm;
	}
	
	public MVPState saveState() {
		return state.copy();
	}
	
	public void loadState(MVPState st) {
		if(st != null) {
			state = st;
		}
	}

	public GLPrimitives getPrimitiveTool() {
		return primitives;
	}

	public void resetViewMatrix() {
		state.viewMatrix = Matrix.identityMatrix(4);
		updateViewMatrix();
	}

	public void rotateCameraX(float ang) {
		applyCameraTransform(Matrix.rotationX(4, ang));
	}

	public void rotateCameraY(float ang) {
		applyCameraTransform(Matrix.rotationY(4, ang));
	}

	public void rotateCameraZ(float ang) {
		applyCameraTransform(Matrix.rotationZ(4, ang));
	}

	public void translateCamera(float x, float y, float z) {
		applyCameraTransform(Matrix.translation(x, y, z));
	}

	public void applyCameraTransform(Matrix mat) {
		state.viewMatrix = mat.multiply(state.viewMatrix);
		updateViewMatrix();
	}

	public void resetModelMatrix() {
		state.modelMatrix = Matrix.identityMatrix(4);
		updateModelViewMatrix();
	}

	public void rotateModelX(float ang) {
		applyModelTransform(Matrix.rotationX(4, ang));
	}

	public void rotateModelY(float ang) {
		applyModelTransform(Matrix.rotationY(4, ang));
	}

	public void rotateModelZ(float ang) {
		applyModelTransform(Matrix.rotationZ(4, ang));
	}

	public void scaleModel(float f) {
		applyModelTransform(Matrix.dilation(f, f, f, 1f));
	}

	public void translateModel(float x, float y, float z) {
		applyModelTransform(Matrix.translation(x, y, z));
	}

	public void applyModelTransform(Matrix mat) {
		state.modelMatrix = mat.multiply(state.modelMatrix);
		updateModelViewMatrix();
	}

	public int loadTexture(ByteBuffer rawData, int height, int width) {
		int texID = glGenTextures();
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, texID);
		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGBA,
				GL_UNSIGNED_BYTE, rawData);
		return texID;
	}

	public int loadTextureArray(ByteBuffer rawData, int number, int height,
			int width) {
		int texID = glGenTextures();
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D_ARRAY, texID);
		glTexStorage3D(GL_TEXTURE_2D_ARRAY, 1, GL_RGBA8, width, height, number);
		glTexSubImage3D(GL_TEXTURE_2D_ARRAY, 0, 0, 0, 0, width, height, number,
				GL_RGBA, GL_UNSIGNED_BYTE, rawData);
		glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_S,
				GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_T,
				GL_CLAMP_TO_EDGE);
		return texID;
	}

	public void bindTextureArray(int location) {
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D_ARRAY, location);
		glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameterf(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameterf(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_T, GL_REPEAT);
	}

	public void bindTexture(int location) {
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, location);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
	}

	public GLShaderHandler getShaders() {
		return shaders;
	}

	public void updateModelUniform(ShaderType st, String name) {
		getShaders().setUniformMatrixValue(st, name, state.modelMatrix);
	}

	public void updateViewUniform(ShaderType st, String name) {
		shaders.setUniformMatrixValue(st, name, state.viewInverseMatrix);
	}

	public void updateModelViewUniform(ShaderType st, String name) {
		shaders.setUniformMatrixValue(st, name, state.modelViewMatrix);
	}

	public void updateProjectionUniform(ShaderType st, String name) {
		shaders.setUniformMatrixValue(st, name, state.viewInverseMatrix);
	}

	public void updateMVPUniform(ShaderType st, String name) {
		shaders.setUniformMatrixValue(st, name, state.modelViewProjectionMatrix);
	}

	public float getAspectRatio() {
		return width / (float) height;
	}

	public void callList(int lst) {
		glCallList(lst);
	}

	public int startQuadList() {
		int lst = glGenLists(1);
		glNewList(lst, GL_COMPILE);
		glBegin(GL_QUADS);
		return lst;
	}

	public int startTriangleList() {
		int lst = glGenLists(1);
		glNewList(lst, GL_COMPILE);
		glBegin(GL_TRIANGLES);
		return lst;
	}

	public void setVertex(int vertLen, FloatBuffer verts) {
		switch (vertLen) {
		case 1:
			glVertex2f(verts.get(0), 0f);
			break;
		case 2:
			glVertex2f(verts.get(0), verts.get(1));
			break;
		case 3:
			glVertex3f(verts.get(0), verts.get(1), verts.get(2));
			break;
		case 4:
			glVertex4f(verts.get(0), verts.get(1), verts.get(2), verts.get(3));
			break;
		}
	}

	public void setTextureCoordinate(int texLen, FloatBuffer texCoord) {
		switch (texLen) {
		case 1:
			glTexCoord1f(texCoord.get(0));
			break;
		case 2:
			glTexCoord2f(texCoord.get(0), texCoord.get(1));
			break;
		case 3:
			glTexCoord3f(texCoord.get(0), texCoord.get(1), texCoord.get(2));
			break;
		case 4:
			glTexCoord4f(texCoord.get(0), texCoord.get(1), texCoord.get(2),
					texCoord.get(3));
			break;
		}
	}

	public void setNormal(int normLen, FloatBuffer normal) {
		switch (normLen) {
		case 1:
			glNormal3f(normal.get(0), 0f, 0f);
			break;
		case 2:
			glNormal3f(normal.get(0), normal.get(1), 0f);
			break;
		case 3:
			glNormal3f(normal.get(0), normal.get(1), normal.get(2));
			break;
		}
	}

	public void endList() {
		glEnd();
		glEndList();
	}

	@Override
	public void start() {
		running = true;
		runThread.start();
	}

	@Override
	public void stop() {
		running = false;
		if (runThread != null) {
			try {
				runThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void addDrawable(Drawable dr) {
		drawables.add(dr);
	}

	@Override
	public void addAllDrawables(Iterable<? extends Drawable> drbls) {
		for (Drawable dr : drbls) {
			addDrawable(dr);
		}
	}

	private void init(ResourceHandler rh) {
		primitives = new GLPrimitives(this);
		state = new MVPState();
		state.modelMatrix = new Matrix(4, 4);
		state.modelMatrix.identity();
		state.viewMatrix = new Matrix(4, 4);
		state.viewMatrix.identity();
		state.modelMatrix = new Matrix(4, 4);
		state.modelMatrix.identity();
		state.projectionMatrix = new Matrix(4, 4);
		state.projectionMatrix.identity();
		updateViewMatrix();
		drawables = new HashSet<Drawable>();
		shaders = new GLShaderHandler(rh);
		width = GraphicsConfiguration.INITIAL_WINDOW_WIDTH;
		height = GraphicsConfiguration.INITIAL_WINDOW_HEIGHT;
		runThread = new Thread(new Runnable() {
			@Override
			public void run() {
				initGL();
				mainLoop();
			}
		});
	}

	private void initGL() {

		errorCallback = errorCallbackPrint(System.err);
		glfwSetErrorCallback(errorCallback);

		if (glfwInit() != GL_TRUE) {
			throw new IllegalStateException("Unable to initialize GLFW");
		}
		glfwDefaultWindowHints(); // optional, the current window hints are
									// already the default
		glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // the window will stay hidden
												// after creation
		glfwWindowHint(GLFW_RESIZABLE, GL_TRUE); // the window will be resizable

		// Create the window
		window = glfwCreateWindow(width, height, "LWJGLGraphics", NULL, NULL);
		if (window == NULL)
			throw new RuntimeException("Failed to create the GLFW window");

		// Setup a key callback. It will be called every time a key is pressed,
		// repeated or released.
		keyCallback = new GLFWKeyCallback() {
			@Override
			public void invoke(long window, int key, int scancode, int action,
					int mods) {
				if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
					exit();
				}
			}
		};
		glfwSetKeyCallback(window, keyCallback);

		windowSizeCallback = new GLFWWindowSizeCallback() {
			@Override
			public void invoke(long window, int width, int height) {
				onResize(width, height);
			}
		};

		glfwSetWindowSizeCallback(window, windowSizeCallback);

		windowCloseCallback = new GLFWWindowCloseCallback() {
			@Override
			public void invoke(long window) {
				exit();
			}
		};

		glfwSetWindowCloseCallback(window, windowCloseCallback);

		// Get the resolution of the primary monitor
		ByteBuffer vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		// Center our window
		glfwSetWindowPos(window, (GLFWvidmode.width(vidmode) - width) / 2,
				(GLFWvidmode.height(vidmode) - height) / 2);

		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		// Enable v-sync
		glfwSwapInterval(0);

		GLContext.createFromCurrent();

		glEnable(GL_TEXTURE_2D_ARRAY);

		glEnable(GL_TEXTURE_3D);

		glEnable(GL_DEPTH_TEST);

		// Make the window visible
		glfwShowWindow(window);

		onResize(width, height);

		// Set the clear color
		glClearColor(0.1f, 0.2f, 1f, 0.0f);

	}

	private void exit() {
		if (control != null) {
			control.exit();
			glfwSetWindowShouldClose(window, GL_FALSE);
		} else {
			running = false;
			glfwSetWindowShouldClose(window, GL_TRUE);
		}
	}

	private void updateMVP() {
		state.modelViewProjectionMatrix = state.projectionMatrix.multiply(state.modelViewMatrix);
	}

	private void updateViewMatrix() {
		state.viewInverseMatrix = state.viewMatrix.inverse();
		updateModelViewMatrix();
	}

	private void updateModelViewMatrix() {
		state.modelViewMatrix = state.viewInverseMatrix.multiply(state.modelMatrix);
		updateMVP();
	}

	private void onResize(int w, int h) {
		width = w;
		height = h;
		glViewport(0, 0, width, height);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		float ar = getAspectRatio();
		float fovScale = .3f;
		glFrustum(-ar * fovScale, ar * fovScale, -1.0 * fovScale,
				1.0 * fovScale, .1f, 10f);
		state.projectionMatrix.perspective(Mathf.PI * .5f, ar, .1f, 10f);
	}

	private void drawAll() {
		for (Drawable dr : drawables) {
			resetViewMatrix();
			resetModelMatrix();
			dr.draw(this);
		}
	}

	private void mainLoop() {
		while (running && glfwWindowShouldClose(window) == GL_FALSE) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the
																// framebuffer

			drawAll();

			glfwSwapBuffers(window); // swap the color buffers
			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();
		}
		running = false;
	}
	
	public class MVPState {
		protected MVPState copy() {
			MVPState state = new MVPState();
			state.projectionMatrix = projectionMatrix;
			state.viewMatrix= viewMatrix;
			state.viewInverseMatrix = viewInverseMatrix;
			state.modelMatrix = modelMatrix;
			state.modelViewMatrix = modelViewMatrix;
			state.modelViewProjectionMatrix = modelViewProjectionMatrix;
			return state;
		}
		protected void load(MVPState other) {
			projectionMatrix = other.projectionMatrix;
			viewMatrix= other.viewMatrix;
			viewInverseMatrix = other.viewInverseMatrix;
			modelMatrix = other.modelMatrix;
			modelViewMatrix = other.modelViewMatrix;
			modelViewProjectionMatrix = other.modelViewProjectionMatrix;
		}
		protected Matrix projectionMatrix;
		protected Matrix viewMatrix;
		protected Matrix viewInverseMatrix;
		protected Matrix modelMatrix;
		protected Matrix modelViewMatrix;
		protected Matrix modelViewProjectionMatrix;
	}

	private boolean running;

	private GLPrimitives primitives;

	private MVPState state;

	private int width;
	private int height;

	private Collection<Drawable> drawables;

	private long window;

	private GLShaderHandler shaders;

	private GLFWErrorCallback errorCallback;
	private GLFWKeyCallback keyCallback;
	private GLFWWindowSizeCallback windowSizeCallback;
	private GLFWWindowCloseCallback windowCloseCallback;

	private ControllerMain control;

	private Thread runThread;

}
