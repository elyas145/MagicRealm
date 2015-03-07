package lwjglview.graphics;

import lwjglview.graphics.shader.GLShaderHandler;
import lwjglview.graphics.shader.ShaderType;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import config.GraphicsConfiguration;
import controller.ControllerMain;
import utils.handler.Handler;
import utils.math.Mathf;
import utils.math.linear.Matrix;
import utils.resources.ResourceHandler;
import view.selection.CursorListener;
import view.selection.CursorSelection;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Semaphore;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.ARBTextureStorage.*;
import static org.lwjgl.opengl.EXTFramebufferObject.*;
import static org.lwjgl.system.MemoryUtil.*;

public final class LWJGLGraphics {

	public static final int LAYER0 = 0;
	public static final int LAYER1 = LAYER0 + 1;
	public static final int LAYER2 = LAYER1 + 1;
	public static final int LAYER3 = LAYER2 + 1;
	public static final int LAYER4 = LAYER3 + 1;
	public static final int LAYER5 = LAYER4 + 1;
	public static final int LAYER6 = LAYER5 + 1;
	public static final int LAYER7 = LAYER6 + 1;
	public static final int LAYER8 = LAYER7 + 1;
	public static final int LAYER9 = LAYER8 + 1;
	public static final int MAX_LAYERS = LAYER9 + 1;

	public LWJGLGraphics(ResourceHandler rh) {
		init(rh);
		control = null;
	}

	public LWJGLGraphics(ResourceHandler rh, ControllerMain cm) {
		init(rh);
		control = cm;
	}

	public void setCursorListener(CursorListener listen) {
		cursorListener = listen;
	}

	public void saveState(MVPState st) {
		synchronized (state) {
			state.copyTo(st);
		}
	}

	public void loadState(MVPState st) {
		synchronized (state) {
			st.copyTo(state);
		}
	}

	public GLPrimitives getPrimitiveTool() {
		return primitives;
	}

	public void resetViewMatrix() {
		synchronized (state) {
			state.viewMatrix.identity();
			updateViewMatrix();
		}
	}

	public void rotateCameraX(float ang) {
		synchronized (buffer) {
			buffer.rotateX(ang);
			applyCameraTransform(buffer);
		}
	}

	public void rotateCameraY(float ang) {
		synchronized (buffer) {
			buffer.rotateY(ang);
			applyCameraTransform(buffer);
		}
	}

	public void rotateCameraZ(float ang) {
		synchronized (buffer) {
			buffer.rotateZ(ang);
			applyCameraTransform(buffer);
		}
	}

	public void translateCamera(float x, float y, float z) {
		synchronized (buffer) {
			buffer.translate(x, y, z);
			applyCameraTransform(buffer);
		}
	}

	public void applyCameraTransform(Matrix mat) {
		synchronized (state) {
			mat.multiply(state.viewMatrix, state.viewMatrix);
			updateViewMatrix();
		}
	}

	public void resetModelMatrix() {
		synchronized (state) {
			state.modelMatrix.identity();
			updateModelViewMatrix();
		}
	}

	public void rotateModelX(float ang) {
		synchronized (buffer) {
			buffer.rotateX(ang);
			applyModelTransform(buffer);
		}
	}

	public void rotateModelY(float ang) {
		synchronized (buffer) {
			buffer.rotateY(ang);
			applyModelTransform(buffer);
		}
	}

	public void rotateModelZ(float ang) {
		synchronized (buffer) {
			buffer.rotateZ(ang);
			applyModelTransform(buffer);
		}
	}

	public void scaleModel(float f) {
		synchronized (buffer) {
			buffer.scale(f, f, f, 1f);
			applyModelTransform(buffer);
		}
	}

	public void translateModel(float x, float y, float z) {
		synchronized (buffer) {
			buffer.translate(x, y, z);
			applyModelTransform(buffer);
		}
	}

	public void applyModelTransform(Matrix mat) {
		synchronized (state) {
			mat.multiply(state.modelMatrix, state.modelMatrix);
			updateModelViewMatrix();
		}
	}

	public void getPixel(int x, int y, ByteBuffer buff) {
		glReadPixels(x, height - y, 1, 1, GL_RGBA, GL_UNSIGNED_BYTE, buff);
	}

	public int createFrameBuffer() {
		IntBuffer buffer = ByteBuffer.allocateDirect(4)
				.order(ByteOrder.nativeOrder()).asIntBuffer();
		glGenFramebuffersEXT(buffer);
		return buffer.get();
	}

	public void bindFrameBuffer(int fb) {
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, fb);
	}

	public void bindMainBuffer() {
		bindFrameBuffer(0);
	}

	public void useFrameBuffer(int fb) {
		bindFrameBuffer(fb);
		glPushAttrib(GL_VIEWPORT_BIT);
		onResize(width, height);
	}

	public void releaseFrameBuffer() {
		bindMainBuffer();
		glPopAttrib();
	}

	public void bindFrameBufferTexture(int fb, int texID) {
		bindFrameBuffer(fb);
		glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT0_EXT,
				GL_TEXTURE_2D, texID, 0);
		releaseFrameBuffer();
	}

	public int generateBufferTexture() {
		return createTexture(width, height);
	}

	public int createTexture(int width, int height) {
		return loadTexture(null, width, height);
	}

	public void setClearColor(float r, float g, float b, float a) {
		glClearColor(r, g, b, a);
	}

	public int loadTexture(ByteBuffer rawData, int height, int width) {
		int texID = glGenTextures();
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, texID);
		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA,
				GL_UNSIGNED_BYTE, rawData);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S,
				GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T,
				GL_CLAMP_TO_EDGE);
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

	public void updateModelUniform(String name) {
		updateModelUniform(getShaders().getType(), name);
	}

	public void updateModelUniform(ShaderType st, String name) {
		getShaders().setUniformMatrixValue(st, name, state.modelMatrix);
	}

	public void updateViewUniform(String name) {
		updateViewUniform(getShaders().getType(), name);
	}

	public void updateViewUniform(ShaderType st, String name) {
		shaders.setUniformMatrixValue(st, name, state.viewInverseMatrix);
	}

	public void updateModelViewUniform(String name) {
		updateModelViewUniform(getShaders().getType(), name);
	}

	public void updateModelViewUniform(ShaderType st, String name) {
		shaders.setUniformMatrixValue(st, name, state.modelViewMatrix);
	}

	public void updateProjectionUniform(String name) {
		updateProjectionUniform(getShaders().getType(), name);
	}

	public void updateProjectionUniform(ShaderType st, String name) {
		shaders.setUniformMatrixValue(st, name, state.viewInverseMatrix);
	}

	public void updateMVPUniform(String name) {
		updateMVPUniform(getShaders().getType(), name);
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

	public int startList() {
		int lst = glGenLists(1);
		glNewList(lst, GL_COMPILE);
		return lst;
	}

	public int startQuadList() {
		int lst = startList();
		startQuads();
		return lst;
	}

	public int startTriangleList() {
		int lst = startList();
		startTriangles();
		return lst;
	}

	public void startTriangles() {
		glBegin(GL_TRIANGLES);
	}

	public void startQuads() {
		glBegin(GL_QUADS);
	}

	public void endPrimitives() {
		glEnd();
	}

	public void endList() {
		endPrimitives();
		glEndList();
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

	public void start() {
		running = true;
		runThread.start();
	}

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

	public void prepareLayer(Handler<LWJGLGraphics> handle, int layer) {
		checkLayer(layer);
		drawables[layer].addPreparation(handle);
	}

	public void clearLayerDepth(int layer) {
		checkLayer(layer);
		drawables[layer].addPreparation(new Handler<LWJGLGraphics>() {

			@Override
			public void handle(LWJGLGraphics graphics) {
				graphics.clearDepthBuffer();
			}

		});
	}

	public void addDrawable(LWJGLDrawable dr) {
		addDrawable(dr, LAYER0);
	}

	public void addDrawable(LWJGLDrawable dr, int layer) {
		checkLayer(layer);
		drawables[layer].addDrawable((LWJGLDrawable) dr);
	}

	public void removeDrawable(LWJGLDrawable dr, int layer) {
		checkLayer(layer);
		drawables[layer].removeDrawable(dr);
	}

	public void addAllDrawables(Iterable<? extends LWJGLDrawable> drbls) {
		for (LWJGLDrawable dr : drbls) {
			addDrawable(dr);
		}
	}

	public void addAllDrawables(Iterable<? extends LWJGLDrawable> drbls,
			int layer) {
		for (LWJGLDrawable dr : drbls) {
			addDrawable(dr, layer);
		}
	}

	public void clearColourBuffer() {
		glClear(GL_COLOR_BUFFER_BIT);
	}

	public void clearDepthBuffer() {
		glClear(GL_DEPTH_BUFFER_BIT);
	}

	public void clearActiveBuffers() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}

	private void init(ResourceHandler rh) {
		self = this;
		primitives = new GLPrimitives(this);
		state = new MVPState();
		updateViewMatrix();
		drawables = new LayerStorage[MAX_LAYERS];
		for (int i = 0; i < drawables.length; ++i) {
			drawables[i] = new LayerStorage();
		}
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
		buffer = Matrix.identity(4);
	}

	private void checkLayer(int layer) {
		if (layer < 0 || layer >= MAX_LAYERS) {
			throw new RuntimeException("The provided layer " + layer
					+ " excedes the maximum number of layers " + MAX_LAYERS);
		}
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

		// Get the resolution of the primary monitor
		ByteBuffer vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

		// Create the window
		width = GLFWvidmode.width(vidmode);
		height = GLFWvidmode.height(vidmode);
		window = glfwCreateWindow(width, height, "LWJGLGraphics",
				glfwGetPrimaryMonitor(), NULL);
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

		mousePositionCallback = new GLFWCursorPosCallback() {

			@Override
			public void invoke(long window, double xpos, double ypos) {
				if (cursorListener != null) {
					cursorListener.onMove((int) xpos, (int) ypos);
				}
			}

		};

		glfwSetCursorPosCallback(window, mousePositionCallback);

		mouseClickCallback = new GLFWMouseButtonCallback() {

			@Override
			public void invoke(long window, int button, int action, int mods) {
				if (cursorListener != null) {
					CursorSelection butt;
					if (button == GLFW_MOUSE_BUTTON_LEFT
							|| button == GLFW_MOUSE_BUTTON_RIGHT) {
						if (button == GLFW_MOUSE_BUTTON_LEFT) {
							butt = CursorSelection.PRIMARY;
						} else {
							butt = CursorSelection.SECONDARY;
						}
						cursorListener.onSelection(butt, action == GLFW_PRESS);
					}
				}
			}

		};

		glfwSetMouseButtonCallback(window, mouseClickCallback);

		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		// Enable v-sync
		glfwSwapInterval(0);

		GLContext.createFromCurrent();

		glEnable(GL_TEXTURE_2D);

		glEnable(GL_TEXTURE_2D_ARRAY);

		glEnable(GL_TEXTURE_3D);

		glEnable(GL_DEPTH_TEST);

		// Make the window visible
		glfwShowWindow(window);

		onResize(width, height);

		// Set the clear color
		setClearColor(.1f, .2f, 1f, 0f);

		System.out.println("Loading shaders");
		GLShaderHandler shaders = getShaders();
		try {
			synchronized (shaders) {
				for (ShaderType st : ShaderType.values()) {
					shaders.loadShaderProgram(st);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		System.out.println("Done loading shaders");

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
		state.projectionMatrix.multiply(state.modelViewMatrix,
				state.modelViewProjectionMatrix);
	}

	private void updateViewMatrix() {
		state.viewMatrix.inverse(state.viewInverseMatrix);
		updateModelViewMatrix();
	}

	private void updateModelViewMatrix() {
		state.viewInverseMatrix.multiply(state.modelMatrix,
				state.modelViewMatrix);
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
		synchronized (state) {
			state.projectionMatrix.perspective(Mathf.PI * .5f, ar, .1f, 10f);
			updateMVP();
		}
	}

	private void drawAll() {
		for (LayerStorage layer : drawables) {
			layer.draw();
		}
	}

	private void mainLoop() {
		while (running && glfwWindowShouldClose(window) == GL_FALSE) {
			drawAll();

			glfwSwapBuffers(window); // swap the color buffers
			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();
		}
		running = false;
	}

	public class MVPState {
		public MVPState() {
			projectionMatrix = Matrix.identity(4);
			viewMatrix = Matrix.identity(4);
			viewInverseMatrix = Matrix.identity(4);
			modelMatrix = Matrix.identity(4);
			modelViewMatrix = Matrix.identity(4);
			modelViewProjectionMatrix = Matrix.identity(4);
		}

		public void copyTo(MVPState state) {
			state.projectionMatrix.copyFrom(projectionMatrix);
			state.viewMatrix.copyFrom(viewMatrix);
			state.viewInverseMatrix.copyFrom(viewInverseMatrix);
			state.modelMatrix.copyFrom(modelMatrix);
			state.modelViewMatrix.copyFrom(modelViewMatrix);
			state.modelViewProjectionMatrix.copyFrom(modelViewProjectionMatrix);
		}

		public String toString() {
			return "Projection: " + projectionMatrix + "\n" + "View: "
					+ viewMatrix + "\n" + "View inverse: " + viewInverseMatrix
					+ "\n" + "Model: " + modelMatrix + "\n" + "ModelView: "
					+ modelViewMatrix + "\n" + "MVP: "
					+ modelViewProjectionMatrix;
		}

		protected Matrix projectionMatrix;
		protected Matrix viewMatrix;
		protected Matrix viewInverseMatrix;
		protected Matrix modelMatrix;
		protected Matrix modelViewMatrix;
		protected Matrix modelViewProjectionMatrix;
	}

	private class LayerStorage {
		private Set<LWJGLDrawable> drawables;
		private List<Handler<LWJGLGraphics>> prepare;

		public LayerStorage() {
			drawables = new HashSet<LWJGLDrawable>();
			prepare = new ArrayList<Handler<LWJGLGraphics>>();
		}

		public void addPreparation(Handler<LWJGLGraphics> prep) {
			prepare.add(prep);
		}

		public void addDrawable(LWJGLDrawable drble) {
			drawables.add(drble);
		}

		public void removeDrawable(LWJGLDrawable drble) {
			drawables.remove(drble);
		}

		public void draw() {
			for (Handler<LWJGLGraphics> prep : prepare) {
				prep.handle(self);
			}
			for (LWJGLDrawable drble : drawables) {
				drble.draw(self);
			}
		}
	}

	private LWJGLGraphics self;

	private boolean running;

	private GLPrimitives primitives;

	private MVPState state;

	private int width;
	private int height;

	private LayerStorage[] drawables;

	private long window;

	private GLShaderHandler shaders;

	private GLFWErrorCallback errorCallback;
	private GLFWKeyCallback keyCallback;
	private GLFWWindowSizeCallback windowSizeCallback;
	private GLFWWindowCloseCallback windowCloseCallback;
	private GLFWCursorPosCallback mousePositionCallback;
	private GLFWMouseButtonCallback mouseClickCallback;

	private CursorListener cursorListener;

	private ControllerMain control;

	private Thread runThread;

	private Matrix buffer;

}
