package lwjglview.graphics;

import lwjglview.graphics.shader.GLShaderHandler;
import lwjglview.graphics.shader.ShaderType;
import model.board.Board;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import utils.math.Mathf;
import utils.math.Matrix;
import utils.resources.ResourceHandler;
import view.graphics.Drawable;
import view.graphics.Graphics;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.HashSet;

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
		LWJGLGraphics gfx = new LWJGLGraphics(rh);
		gfx.addDrawable(new LWJGLBoardDrawable(new Board(), rh));
		gfx.start();
	}

	public LWJGLGraphics(ResourceHandler rh) {
		init(rh);
	}

	public GLPrimitives getPrimitiveTool() {
		return primitives;
	}

	public void resetViewMatrix() {
		viewMatrix.identity();
		updateViewMatrix();
	}

	public void rotateCameraX(float ang) {
		viewMatrix = Matrix.rotationX(4, ang).multiply(viewMatrix);
		updateViewMatrix();
	}

	public void rotateCameraY(float ang) {
		viewMatrix = Matrix.rotationY(4, ang).multiply(viewMatrix);
		updateViewMatrix();
	}

	public void rotateCameraZ(float ang) {
		viewMatrix = Matrix.rotationZ(4, ang).multiply(viewMatrix);
		updateViewMatrix();
	}

	public void translateCamera(float x, float y, float z) {
		viewMatrix = Matrix.translation(new float[] {
				x, y, z
		}).multiply(viewMatrix);
		updateViewMatrix();
	}
	
	public void applyCameraTransform(Matrix mat) {
		viewMatrix = mat.multiply(viewMatrix);
		updateViewMatrix();
	}

	public void resetModelMatrix() {
		modelMatrix.identity();
		updateModelViewMatrix();
	}

	public void rotateModelX(float ang) {
		modelMatrix = Matrix.rotationX(4, ang).multiply(modelMatrix);
		updateModelViewMatrix();
	}

	public void rotateModelY(float ang) {
		modelMatrix = Matrix.rotationY(4, ang).multiply(modelMatrix);
		updateModelViewMatrix();
	}

	public void rotateModelZ(float ang) {
		modelMatrix = Matrix.rotationZ(4, ang).multiply(modelMatrix);
		updateModelViewMatrix();
	}
	
	public void applyModelTransform(Matrix mat) {
		modelMatrix = mat.multiply(modelMatrix);
		updateModelViewMatrix();
	}

	public void translateModel(float x, float y, float z) {
		modelMatrix = Matrix.translation(new float[] {
				x, y, z
		}).multiply(modelMatrix);
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
		shaders.setUniformMatrixValue(st, name, modelMatrix);
	}
	
	public void updateViewUniform(ShaderType st, String name) {
		shaders.setUniformMatrixValue(st, name, viewInverseMatrix);
	}
	
	public void updateModelViewUniform(ShaderType st, String name) {
		shaders.setUniformMatrixValue(st, name, modelViewMatrix);
	}
	
	public void updateProjectionUniform(ShaderType st, String name) {
		shaders.setUniformMatrixValue(st, name, viewInverseMatrix);
	}
	
	public void updateMVPUniform(ShaderType st, String name) {
		shaders.setUniformMatrixValue(st, name, modelViewProjectionMatrix);
	}
	
	public float getAspectRatio() {
		return width / (float) height;
	}

	@Override
	public void start() {
		running = true;
		initGL();
		mainLoop();
	}

	@Override
	public void stop() {
		running = false;
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
		primitives = new GLPrimitives();
		modelMatrix = new Matrix(4, 4);
		modelMatrix.identity();
		viewMatrix = new Matrix(4, 4);
		viewMatrix.identity();
		modelMatrix = new Matrix(4, 4);
		modelMatrix.identity();
		projectionMatrix = new Matrix(4, 4);
		projectionMatrix.identity();
		updateViewMatrix();
		drawables = new HashSet<Drawable>();
		shaders = new GLShaderHandler(rh);
		width = 300;
		height = 300;
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
					glfwSetWindowShouldClose(window, GL_TRUE);
				}
			}
		};
		glfwSetKeyCallback(window, keyCallback);

		windowCallback = new GLFWWindowSizeCallback() {
			@Override
			public void invoke(long window, int width, int height) {
				onResize(width, height);
			}
		};

		glfwSetWindowSizeCallback(window, windowCallback);

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
	
	private void updateMVP() {
		modelViewProjectionMatrix =
				projectionMatrix.multiply(modelViewMatrix);
	}
	
	private void updateViewMatrix() {
		viewInverseMatrix = viewMatrix.inverse();
		updateModelViewMatrix();
	}
	
	private void updateModelViewMatrix() {
		modelViewMatrix = viewInverseMatrix.multiply(modelMatrix);
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
		projectionMatrix.perspective(Mathf.PI * .5f, ar, .1f, 10f);
	}

	private void drawAll() {
		for (Drawable dr : drawables) {
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

	private boolean running;

	private GLPrimitives primitives;
	
	private Matrix projectionMatrix;
	private Matrix viewMatrix;
	private Matrix viewInverseMatrix;
	private Matrix modelMatrix;
	private Matrix modelViewMatrix;
	private Matrix modelViewProjectionMatrix;
	
	private int width;
	private int height;

	private Collection<Drawable> drawables;

	private long window;

	private GLShaderHandler shaders;

	private GLFWErrorCallback errorCallback;
	private GLFWKeyCallback keyCallback;
	private GLFWWindowSizeCallback windowCallback;

}
