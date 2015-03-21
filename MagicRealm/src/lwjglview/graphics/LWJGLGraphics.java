package lwjglview.graphics;

import lwjglview.graphics.shader.GLShaderHandler;
import lwjglview.graphics.shader.ShaderType;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import client.ControllerMain;
import config.GraphicsConfiguration;
import utils.handler.Handler;
import utils.math.Mathf;
import utils.math.linear.Matrix;
import utils.resources.ResourceHandler;
import view.selection.CursorListener;
import view.selection.CursorSelection;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL30.*;
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

	public void enableDepth() {
		glEnable(GL_DEPTH_TEST);
	}

	public void disableDepth() {
		glDisable(GL_DEPTH_TEST);
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

	public int createFrameBuffer(int width, int height) {
		int buff = glGenFramebuffers();
		frameBuffers.put(buff, new FrameBufferInfo(width, height));
		return buff;
	}

	public int createFrameBuffer() {
		return createFrameBuffer(width, height);
	}

	public void bindFrameBuffer(int fb) {
		glBindFramebuffer(GL_FRAMEBUFFER, fb);
	}

	public void bindMainBuffer() {
		bindFrameBuffer(0);
	}

	public void useFrameBuffer(int fb) {
		bindFrameBuffer(fb);
		FrameBufferInfo info = frameBuffers.get(fb);
		onResize(info.width, info.height);
	}

	public void releaseFrameBuffer() {
		useFrameBuffer(0);
	}

	public void bindFrameBufferTexture(int fb, int texID) {
		bindFrameBuffer(fb);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0,
				GL_TEXTURE_2D, texID, 0);
		int depth = glGenRenderbuffers();
		glBindRenderbuffer(GL_RENDERBUFFER, depth);
		glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT24,
				width, height);
		glFramebufferRenderbuffer(GL_FRAMEBUFFER,
				GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depth);
		releaseFrameBuffer();
	}

	public int generateBufferTexture(int buff) {
		FrameBufferInfo fbi = frameBuffers.get(buff);
		int id = createTexture(fbi.width, fbi.height, false);
		bindFrameBufferTexture(buff, id);
		return id;
	}

	public int createTexture(int width, int height, boolean accurate) {
		return loadTexture(null, height, width, accurate);
	}

	public void setClearColor(float r, float g, float b, float a) {
		glClearColor(r, g, b, a);
	}

	public int loadTexture(ByteBuffer rawData, int height, int width,
			boolean accurate) {
		int texID = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, texID);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER,
				accurate ? GL_LINEAR : GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER,
				accurate ? GL_LINEAR : GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA,
				GL_UNSIGNED_BYTE, rawData);
		return texID;
	}

	public int loadTextureArray(ByteBuffer rawData, int number, int height,
			int width, boolean accurate) {
		int texID = glGenTextures();
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D_ARRAY, texID);
		/*
		 * glTexStorage3D(GL_TEXTURE_2D_ARRAY, 1, GL_RGBA8, width, height,
		 * number); glTexSubImage3D(GL_TEXTURE_2D_ARRAY, 0, 0, 0, 0, width,
		 * height, number, GL_RGBA, GL_UNSIGNED_BYTE, rawData);
		 */
		glTexImage3D(GL_TEXTURE_2D_ARRAY, 0, GL_RGB8, width, height, number, 0,
				GL_RGBA, GL_UNSIGNED_BYTE, rawData);
		glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MIN_FILTER,
				accurate ? GL_LINEAR : GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MAG_FILTER,
				accurate ? GL_LINEAR : GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_S,
				GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_T,
				GL_CLAMP_TO_EDGE);
		return texID;
	}

	public void bindTextureArray(int location) {
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D_ARRAY, location);
	}

	public void bindTexture(int location) {
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, location);
	}

	public void bindTexture(int location, int unit) {
		glActiveTexture(GL_TEXTURE0 + unit);
		glBindTexture(GL_TEXTURE_2D, location);
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

	public void finishLayer(Handler<LWJGLGraphics> handle, int layer) {
		checkLayer(layer);
		drawables[layer].addFinalization(handle);
	}

	public void enableLayerDepth(int layer) {
		checkLayer(layer);
		drawables[layer].addPreparation(new Handler<LWJGLGraphics>() {

			@Override
			public void handle(LWJGLGraphics gfx) {
				gfx.enableDepth();
			}

		});
	}

	public void disableLayerDepth(int layer) {
		checkLayer(layer);
		drawables[layer].addPreparation(new Handler<LWJGLGraphics>() {

			@Override
			public void handle(LWJGLGraphics gfx) {
				gfx.disableDepth();
			}

		});
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
		frameBuffers = new HashMap<Integer, FrameBufferInfo>();
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
		frameBuffers.put(0, new FrameBufferInfo(width, height));
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

		glEnable(GL_BLEND);

		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

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
		float ar = getAspectRatio();
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
		private List<Handler<LWJGLGraphics>> finalize;

		public LayerStorage() {
			drawables = new HashSet<LWJGLDrawable>();
			prepare = new ArrayList<Handler<LWJGLGraphics>>();
			finalize = new ArrayList<Handler<LWJGLGraphics>>();
		}

		public void addFinalization(Handler<LWJGLGraphics> fin) {
			synchronized (finalize) {
				finalize.add(fin);
			}
		}

		public void addPreparation(Handler<LWJGLGraphics> prep) {
			synchronized (prepare) {
				prepare.add(prep);
			}
		}

		public void addDrawable(LWJGLDrawable drble) {
			synchronized (drawables) {
				drawables.add(drble);
			}
		}

		public void removeDrawable(LWJGLDrawable drble) {
			synchronized (drawables) {
				drawables.remove(drble);
			}
		}

		public void draw() {
			synchronized (prepare) {
				for (Handler<LWJGLGraphics> prep : prepare) {
					prep.handle(self);
				}
			}
			synchronized (drawables) {
				for (LWJGLDrawable drble : drawables) {
					drble.draw(self);
				}
			}
			synchronized (finalize) {
				for (Handler<LWJGLGraphics> fin : finalize) {
					fin.handle(self);
				}
			}
		}
	}

	private static class FrameBufferInfo {
		public FrameBufferInfo(int w, int h) {
			width = w;
			height = h;
		}

		public int width, height;
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

	private Map<Integer, FrameBufferInfo> frameBuffers;

	private CursorListener cursorListener;

	private ControllerMain control;

	private Thread runThread;

	private Matrix buffer;

}
