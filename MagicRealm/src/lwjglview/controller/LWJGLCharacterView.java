package lwjglview.controller;

import java.nio.FloatBuffer;

import utils.handler.Handler;
import utils.math.Mathf;
import utils.math.linear.Matrix;
import utils.resources.ResourceHandler;
import config.GraphicsConfiguration;
import lwjglview.graphics.LWJGLGraphics;
import lwjglview.graphics.counters.LWJGLCounterDrawable;
import lwjglview.graphics.counters.LWJGLCounterLocator;
import lwjglview.graphics.shader.GLShaderHandler;
import lwjglview.graphics.shader.ShaderType;
import lwjglview.graphics.textures.LWJGLSingleTextureLoader;
import lwjglview.menus.LWJGLContentPane;
import lwjglview.menus.LWJGLPanel;
import model.enums.CharacterType;

public class LWJGLCharacterView extends LWJGLCounterLocator {

	private static final int WIDTH = 500;
	private static final int HEIGHT = 500;

	public LWJGLCharacterView(CharacterType character, ResourceHandler rh,
			LWJGLGraphics gfx) {
		super(null);
		graphics = gfx;
		bufferLoc = -1;
		textureLoc = -1;
		counter = rh.getCounterGenerator().generate(
				character.toCounter(), this);
		counter.moveTo(Matrix.columnVector(0f, 0f, 0f));
		graphics.addDrawable(this, GraphicsConfiguration.PRE_RENDER_LAYER);
	}

	public LWJGLPanel getPanel(LWJGLContentPane parent, float x, float y,
			float size, boolean select) {
		return new LWJGLPanel(parent, new LWJGLSingleTextureLoader(textureLoc,
				WIDTH, HEIGHT) {
			@Override
			public int getTextureLocation() {
				return textureLoc;
			}
		}, x, y, size, select);
	}

	@Override
	public void draw(LWJGLGraphics gfx) {
		if (bufferLoc < 0) {
			bufferLoc = gfx.createFrameBuffer(WIDTH, HEIGHT);
			textureLoc = gfx.generateBufferTexture(bufferLoc);
		}
		gfx.useFrameBuffer(bufferLoc);
		gfx.setClearColor(0f, 1f, 1f, .8f);
		gfx.clearActiveBuffers();
		gfx.resetViewMatrix();
		gfx.translateCamera(0f, 0f, -0.3f);
		gfx.rotateCameraX(-Mathf.PI / 4);
		updateTransformation();
		counter.draw(gfx);
		gfx.releaseFrameBuffer();
	}

	@Override
	public void getCounterPosition(LWJGLCounterDrawable cd, Matrix pos) {
		pos.translate(0f, 0f, 0f);
	}

	@Override
	public int addCounterDrawable(LWJGLCounterDrawable counter) {
		return 0;
	}

	@Override
	public void removeCounterDrawable(LWJGLCounterDrawable cd) {
	}

	@Override
	public void updateNodeUniforms(LWJGLGraphics gfx) {
		GLShaderHandler shaders = gfx.getShaders();
		shaders.useShaderProgram(ShaderType.CHIT_SHADER);
		shaders.setUniformIntValue("index", -1);
		shaders.setUniformFloatArrayValue("ambientColour", 4, FloatBuffer.wrap(new float[] {
				1f, 1f, 1f, 1f
		}));
	}

	private LWJGLGraphics graphics;
	private LWJGLCounterDrawable counter;
	private int bufferLoc;
	private int textureLoc;

}
