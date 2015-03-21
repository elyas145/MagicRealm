package lwjglview.menus;

import java.util.ArrayList;
import java.util.List;

import utils.handler.Handler;
import utils.math.linear.Matrix;
import config.GraphicsConfiguration;
import lwjglview.graphics.LWJGLGraphics;
import lwjglview.graphics.animator.matrixcalculator.MatrixCalculator;
import lwjglview.graphics.shader.ShaderType;
import lwjglview.selection.SelectionFrame;
import lwjglview.selection.SelectionShaderType;

public class LWJGLMenuLayer extends LWJGLContentPane {

	public LWJGLMenuLayer(LWJGLGraphics gfx, SelectionFrame frame) {
		super(null, new AspectCalculator(gfx));
		selectionFrame = frame;
		panels = new ArrayList<LWJGLPanel>();

		gfx.disableLayerDepth(GraphicsConfiguration.MENUS_DISPLAY_LAYER);
		gfx.prepareLayer(new Handler<LWJGLGraphics>() {

			@Override
			public void handle(LWJGLGraphics gfx) {
				gfx.clearDepthBuffer();
				gfx.getShaders().useShaderProgram(ShaderType.ORTHO_SHADER);
				gfx.resetViewMatrix();
			}

		}, GraphicsConfiguration.MENUS_DISPLAY_LAYER);

		gfx.disableLayerDepth(GraphicsConfiguration.MENUS_SELECTION_LAYER);
		gfx.prepareLayer(new Handler<LWJGLGraphics>() {

			@Override
			public void handle(LWJGLGraphics gfx) {
				gfx.clearDepthBuffer();
				selectionFrame.useShader(gfx.getShaders(),
						SelectionShaderType.MENU);
				gfx.resetViewMatrix();
			}

		}, GraphicsConfiguration.MENUS_SELECTION_LAYER);

		gfx.addDrawable(this, GraphicsConfiguration.MENUS_DISPLAY_LAYER);
		gfx.addDrawable(this, GraphicsConfiguration.MENUS_SELECTION_LAYER);
	}

	public SelectionFrame getSelectionFrame() {
		return selectionFrame;
	}

	@Override
	public void add(LWJGLPanel pane) {
		synchronized(panels) {
			panels.add(pane);
		}
		pane.setParent(this);
	}

	@Override
	public void remove(LWJGLPanel pane) {
		synchronized(panels) {
			panels.remove(pane);
		}
	}

	@Override
	public void updateNodeUniforms(LWJGLGraphics gfx) {
	}

	@Override
	public void draw(LWJGLGraphics gfx) {
		updateTransformation();

		synchronized (panels) {
			for (LWJGLPanel pane : panels) {
				pane.draw(gfx);
			}
		}
	}

	private static class AspectCalculator implements MatrixCalculator {

		public AspectCalculator(LWJGLGraphics gfx) {
			graphics = gfx;
			buffer = Matrix
					.dilation(1f / graphics.getAspectRatio(), 1f, 1f, 1f);
		}

		@Override
		public Matrix calculateMatrix() {
			buffer.scale(1f / graphics.getAspectRatio(), 1f, 1f, 1f);
			return buffer;
		}

		private Matrix buffer;
		private LWJGLGraphics graphics;

	}

	private SelectionFrame selectionFrame;
	private List<LWJGLPanel> panels;

	private int activeBuffer;
	private int backBuffer;
	private boolean swappedBuffers;

}
