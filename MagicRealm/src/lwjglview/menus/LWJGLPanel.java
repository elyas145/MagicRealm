package lwjglview.menus;

import java.util.ArrayList;
import java.util.List;

import utils.handler.Handler;
import utils.math.linear.Matrix;
import utils.resources.ResourceHandler;
import view.selection.CursorListener;
import view.selection.CursorSelection;
import lwjglview.graphics.GLPrimitives;
import lwjglview.graphics.LWJGLDrawableLeaf;
import lwjglview.graphics.LWJGLDrawableNode;
import lwjglview.graphics.LWJGLGraphics;
import lwjglview.graphics.LWJGLTextureLoader;
import lwjglview.graphics.animator.matrixcalculator.StaticMatrixCalculator;
import lwjglview.graphics.shader.ShaderType;
import lwjglview.selection.SelectionFrame;

public class LWJGLPanel extends LWJGLDrawableNode {
	
	public static void main(String[] args) {
		ResourceHandler rh = new ResourceHandler();
		LWJGLGraphics gfx = new LWJGLGraphics(rh);
		SelectionFrame sf = new SelectionFrame(gfx);
		LWJGLTextureLoader text = new LWJGLTextureLoader(rh, "test.jpg");
		LWJGLPanel panel1 = new LWJGLPanel(null, sf, text, 0f, 0f, 0f, .5f, 1f);
		gfx.prepareLayer(new Handler<LWJGLGraphics>() {

			@Override
			public void handle(LWJGLGraphics gfx) {
				gfx.getShaders().useShaderProgram(ShaderType.ORTHO_SHADER);
				gfx.clearActiveBuffers();
			}
			
		}, LWJGLGraphics.LAYER0);
		gfx.addDrawable(panel1, LWJGLGraphics.LAYER0);
		gfx.start();
	}

	public LWJGLPanel(LWJGLDrawableNode par, SelectionFrame select,
			LWJGLTextureLoader text, float xPos, float yPos, float zPos,
			float w, float h) {
		super(par);
		init(null, select, text, xPos, yPos, zPos, w, h);
	}

	@Override
	public void draw(LWJGLGraphics gfx) {
		updateTransformation();
		
		for(LWJGLDrawableNode child: children) {
			child.draw(gfx);
		}
	}

	@Override
	public void updateNodeUniforms(LWJGLGraphics gfx) {
		if (selectFrame.isSelectionPass()) {
			selectFrame.loadID(selectionID, gfx);
		} else {
			texture.useTexture(gfx);
		}
	}
	
	protected void add(LWJGLPanel other) {
		children.add(other);
		other.setParent(this);
	}
	
	private void init(LWJGLPanel pane, SelectionFrame select, LWJGLTextureLoader text, float xPos, float yPos, float zPos, float w, float h) {
		buffer = Matrix.translation(1f, -1f, zPos);
		transformation = Matrix.dilation(w * .5f, -h * .5f, 1f, 1f);
		transformation.multiply(buffer, buffer);
		transformation.translate(xPos, yPos, zPos);
		texture = text;
		selectFrame = select;
		selectionID = select.getNewID(new CursorListener() {

			@Override
			public void onMove(int x, int y) {
				move(x, y);
			}

			@Override
			public void onSelection(CursorSelection select, boolean down) {
				selection(select, down);
			}

		});
		setCalculator(new StaticMatrixCalculator(transformation));
		children = new ArrayList<LWJGLDrawableNode>();
		children.add(new LWJGLDrawableLeaf(this, buffer, GLPrimitives.SQUARE) {
			@Override
			public void updateNodeUniforms(LWJGLGraphics lwgfx) {
				lwgfx.updateModelViewUniform("modelViewMatrix");
				lwgfx.updateMVPUniform("mvpMatrix");
				if(selectFrame.isSelectionPass()) {
					selectFrame.loadID(selectionID, lwgfx);
				}
				else {
					texture.useTexture(lwgfx);
				}
			}
		});
		if(pane != null) {
			pane.add(this);
		}
	}

	private void move(int x, int y) {

	}

	private void selection(CursorSelection select, boolean down) {

	}

	private List<LWJGLDrawableNode> children;
	private Matrix transformation;
	private Matrix buffer;
	private LWJGLTextureLoader texture;
	private SelectionFrame selectFrame;
	private int selectionID;

}
