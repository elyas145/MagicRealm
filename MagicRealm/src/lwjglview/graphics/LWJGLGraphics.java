package lwjglview.graphics;

import model.board.HexTile;
import model.board.TileType;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import utils.math.Matrix;
import utils.resources.ResourceHandler;
import view.graphics.Drawable;
import view.graphics.Graphics;
 
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.HashSet;

 
import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

public final class LWJGLGraphics implements Graphics {
	
	public static void main(String[] args) {
		LWJGLGraphics gfx = new LWJGLGraphics(null);
		for(int i = 0; i < 20; ++i) {
			gfx.addDrawable(new LWJGLTileDrawable(
					new HexTile(TileType.BAD_LANDS, (int)(Math.random()*5), (int)(Math.random()*5))));
		}
		gfx.start();
	}

	public LWJGLGraphics(ResourceHandler rh) {
		init(rh);
	}
	
	public GLPrimitives getPrimitiveTool() {
		return primitives;
	}

	public void resetModel() {
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
	}

	public void translateModel(float x, float y, float z) {
		glTranslatef(x, y, z);
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
		for(Drawable dr: drbls) {
			addDrawable(dr);
		}
	}
	
	private void init(ResourceHandler rh) {
		primitives = new GLPrimitives();
		camera = new Matrix(4, 4);
		camera.identity();
		drawables = new HashSet<Drawable>();
	}
	
	private void initGL() {
		errorCallback = errorCallbackPrint(System.err);
		glfwSetErrorCallback(errorCallback);
		
		if ( glfwInit() != GL11.GL_TRUE ) {
            throw new IllegalStateException("Unable to initialize GLFW");
		}
		glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE); // the window will be resizable
 
        int width = 300;
        int height = 300;
 
        // Create the window
        window = glfwCreateWindow(width, height, "LWJGLGraphics", NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");
 
        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if(key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                    glfwSetWindowShouldClose(window, GL_TRUE); // We will detect this in our rendering loop
                }
            }
        };
        glfwSetKeyCallback(window, keyCallback);
        
    	glfwSetWindowSizeCallback(window, new GLFWWindowSizeCallback() {
			@Override
			public void invoke(long window, int width, int height) {
				onResize(width, height);
			}
    	});
    	
        // Get the resolution of the primary monitor
        ByteBuffer vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        // Center our window
        glfwSetWindowPos(
            window,
            (GLFWvidmode.width(vidmode) - width) / 2,
            (GLFWvidmode.height(vidmode) - height) / 2
        );
 
        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(0);
        
        GLContext.createFromCurrent();
 
        // Make the window visible
        glfwShowWindow(window);
        
        onResize(width, height);
 
        // Set the clear color
        glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
	}
	
	private void onResize(int width, int height) {
		glViewport(0, 0, width, height);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		float ar = width / (float) height;
		glFrustum(-ar, ar, -1.0, 1.0, .1f, 10f);
	}
	
	private void drawAll() {
		for(Drawable dr: drawables) {
			dr.draw(this);
		}
	}
	
	private void mainLoop() {
		while(running && glfwWindowShouldClose(window) == GL_FALSE) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
            
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
	
	private Matrix camera;
	
	private Collection<Drawable> drawables;
	
	private long window;

    private GLFWErrorCallback errorCallback;
    private GLFWKeyCallback   keyCallback;
    
}
