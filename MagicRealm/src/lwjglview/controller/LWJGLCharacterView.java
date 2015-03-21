package lwjglview.controller;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import utils.handler.Handler;
import utils.math.linear.Matrix;
import utils.resources.ResourceHandler;
import config.GraphicsConfiguration;
import lwjglview.graphics.LWJGLDrawable;
import lwjglview.graphics.LWJGLGraphics;
import lwjglview.graphics.LWJGLTextureLoader;
import lwjglview.graphics.animator.Animator;
import lwjglview.graphics.animator.TimeAnimator;
import lwjglview.graphics.model.ModelData;
import lwjglview.graphics.shader.GLShaderHandler;
import lwjglview.graphics.shader.ShaderType;
import lwjglview.menus.LWJGLContentPane;
import lwjglview.menus.LWJGLPanel;

public class LWJGLCharacterView {

	private static final int WIDTH = 700;
	private static final int HEIGHT = 700;

	public LWJGLCharacterView(ResourceHandler rh, LWJGLGraphics gfx) {
		graphics = gfx;
		bufferLoc = -1;
		textureLoc = -1;
		try {
			counter = ModelData.loadModelData(rh, "circle_counter.obj");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		matrix = Matrix.identity(4);
		rotation = new TimeAnimator(1f) {

			@Override
			public boolean isFinished() {
				return false;
			}

			@Override
			public void finish() {
			}

			@Override
			protected Matrix calculateTransform() {
				matrix.rotateZ(getTime());
				return matrix;
			}

		};
		rotation.start();
		graphics.addDrawable(new LWJGLDrawable() {

			@Override
			public void updateUniforms(LWJGLGraphics gfx) {
				if (bufferLoc < 0) {
					bufferLoc = gfx.createFrameBuffer(WIDTH, HEIGHT);
					textureLoc = gfx.generateBufferTexture(bufferLoc);
				}
				gfx.useFrameBuffer(bufferLoc);
				gfx.setClearColor(1f, 1f, 1f, .8f);
				gfx.clearActiveBuffers();
				GLShaderHandler shaders = gfx.getShaders();
				shaders.useShaderProgram(ShaderType.CHIT_SHADER);
				gfx.resetViewMatrix();
				gfx.resetModelMatrix();
				gfx.applyModelTransform(rotation.apply());
				gfx.rotateModelX(1f);
				gfx.translateModel(0f, 0f, -1f);
				shaders.setUniformIntValue("index", -1);
				FloatBuffer col = BufferUtils.createFloatBuffer(4);
				col.put(0, 1f);
				col.put(1, 1f);
				col.put(2, 1f);
				col.put(3, 1f);
				shaders.setUniformFloatArrayValue("counterColour", 4, col);
				shaders.setUniformFloatArrayValue("ambientColour", 4, col);
				gfx.updateModelViewUniform("modelViewMatrix");
				gfx.updateMVPUniform("mvpMatrix");
			}

			@Override
			public void draw(LWJGLGraphics gfx) {
				updateUniforms(gfx);
				counter.draw(gfx);
			}

		}, GraphicsConfiguration.PRE_RENDER_LAYER);
		graphics.finishLayer(new Handler<LWJGLGraphics>() {

			@Override
			public void handle(LWJGLGraphics gfx) {
				gfx.releaseFrameBuffer();
			}
			
		}, GraphicsConfiguration.PRE_RENDER_LAYER);
	}

	public LWJGLPanel getPanel(LWJGLContentPane parent, float x, float y,
			float size, boolean select) {
		return new LWJGLPanel(parent, new LWJGLTextureLoader(0, WIDTH,
				HEIGHT) {
			@Override
			public int getTextureLocation() {
				return textureLoc;
			}
		}, x, y, size, select);
	}

	private LWJGLGraphics graphics;
	private ModelData counter;
	private int bufferLoc;
	private int textureLoc;
	private Animator rotation;
	private Matrix matrix;

}
