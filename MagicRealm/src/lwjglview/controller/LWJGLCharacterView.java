package lwjglview.controller;

import utils.handler.Handler;
import utils.resources.ResourceHandler;
import config.GraphicsConfiguration;
import lwjglview.graphics.LWJGLDrawable;
import lwjglview.graphics.LWJGLGraphics;
import lwjglview.graphics.LWJGLTextureLoader;
import lwjglview.graphics.model.ModelData;
import lwjglview.graphics.shader.GLShaderHandler;
import lwjglview.graphics.shader.ShaderType;
import lwjglview.menus.LWJGLContentPane;
import lwjglview.menus.LWJGLPanel;

public class LWJGLCharacterView {

	private static final int WIDTH = 100;
	private static final int HEIGHT = 100;

	public LWJGLCharacterView(ResourceHandler rh, LWJGLGraphics gfx) {
		graphics = gfx;
		bufferLoc = -1;
		textureLoc = -1;
		try {
			counter = ModelData.loadModelData(rh, "circle_counter.obj");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		graphics.prepareLayer(new Handler<LWJGLGraphics>() {

			@Override
			public void handle(LWJGLGraphics gfx) {
				gfx.setClearColor(1f, 0f, 0f, 1f);
				gfx.clearActiveBuffers();
			}
			
		}, GraphicsConfiguration.BOARD_DISPLAY_LAYER);
		graphics.addDrawable(new LWJGLDrawable() {

			@Override
			public void updateUniforms(LWJGLGraphics gfx) {
				if (bufferLoc < 0) {
					bufferLoc = gfx.createFrameBuffer(WIDTH, HEIGHT);
					textureLoc = gfx.generateBufferTexture(bufferLoc);
				}
				gfx.useFrameBuffer(bufferLoc);
				gfx.setClearColor(1f, 0f, 0f, 0f);
				gfx.clearColourBuffer();
				gfx.resetViewMatrix();
				gfx.resetModelMatrix();
				gfx.translateModel(0f, 0f, -10f);
				GLShaderHandler shaders = gfx.getShaders();
				shaders.useShaderProgram(ShaderType.CHIT_SHADER);
				shaders.setUniformIntValue("index", -1);
				gfx.updateModelViewUniform("modelViewMatrix");
				gfx.updateMVPUniform("mvpMatrix");
				gfx.bindTexture(textureLoc);
			}

			@Override
			public void draw(LWJGLGraphics gfx) {
				updateUniforms(gfx);
				counter.draw(gfx);
				gfx.releaseFrameBuffer();
			}

		}, GraphicsConfiguration.PRE_RENDER_LAYER);
	}
	
	public LWJGLPanel getPanel(LWJGLContentPane parent, float x, float y, float size, boolean select) {
		return new LWJGLPanel(parent, new LWJGLTextureLoader(textureLoc, WIDTH, HEIGHT), x, y, size, select);
	}

	private LWJGLGraphics graphics;
	private ModelData counter;
	private int bufferLoc;
	private int textureLoc;

}
