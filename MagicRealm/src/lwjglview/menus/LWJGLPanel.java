package lwjglview.menus;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import utils.images.ImageTools;
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
import lwjglview.selection.SelectionFrame;

public class LWJGLPanel extends LWJGLContentPane {

	public static LWJGLPanel fromTextureLoader(LWJGLContentPane cp,
			LWJGLTextureLoader texture, float x, float y, float size) {
		size /= texture.getHeight();
		return new LWJGLPanel(cp, texture, x, y, texture.getWidth() * size,
				texture.getHeight() * size);
	}

	public static LWJGLPanel fromPicture(LWJGLContentPane cp,
			ResourceHandler rh, String path, float x, float y, float size) {
		LWJGLTextureLoader texture = new LWJGLTextureLoader(rh, path);
		return LWJGLPanel.fromTextureLoader(cp, texture, x, y, size);
	}

	public static LWJGLPanel fromGraphics(LWJGLContentPane cp,
			ImageTools.GraphicsHandler gh, int width, int height, float x,
			float y, float size) {
		LWJGLTextureLoader texture = new LWJGLTextureLoader(gh, width, height);
		return LWJGLPanel.fromTextureLoader(cp, texture, x, y, size);
	}

	public static LWJGLPanel fromString(LWJGLContentPane cp, String str,
			Font fnt, Color col, int height, int width, float x, float y, float size) {
		return LWJGLPanel.fromGraphics(cp,
				new ImageTools.StringDrawer(str, fnt, col), width, height, x, y,
				size);
	}

	public LWJGLPanel(LWJGLContentPane par, LWJGLTextureLoader text,
			float xPos, float yPos, float w, float h) {
		super(par);
		init(par, par.getSelectionFrame(), text, xPos, yPos, w, h);
	}

	public void setCursorListener(CursorListener cl) {
		listener = cl;
	}

	@Override
	public void add(LWJGLPanel other) {
		children.add(other);
		other.setParent(this);
	}

	@Override
	public SelectionFrame getSelectionFrame() {
		return parent.getSelectionFrame();
	}

	@Override
	public void draw(LWJGLGraphics gfx) {
		updateTransformation();

		SelectionFrame sf = getSelectionFrame();

		if (sf.isSelectionPass()) {
			sf.loadID(selectionID, gfx);
		} else {
			texture.useTexture(gfx);
		}
		representation.draw(gfx);

		for (LWJGLDrawableNode child : children) {
			child.draw(gfx);
		}
	}

	@Override
	public void updateNodeUniforms(LWJGLGraphics gfx) {
		gfx.updateModelViewUniform("modelViewMatrix");
	}

	private void init(LWJGLContentPane pane, SelectionFrame select,
			LWJGLTextureLoader text, float xPos, float yPos, float w, float h) {
		parent = pane;
		buffer = Matrix.translation(1f, 1f, 0f);
		transformation = Matrix.dilation(w * .5f, h * .5f, 1f, 1f);
		transformation.multiply(buffer, buffer);
		transformation.translate(xPos, yPos, 0f);
		texture = text;
		selectionID = getSelectionFrame().getNewID(new CursorListener() {

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
		representation = new LWJGLDrawableLeaf(this, buffer,
				GLPrimitives.SQUARE) {
			@Override
			public void updateNodeUniforms(LWJGLGraphics lwgfx) {
				lwgfx.updateModelViewUniform("modelViewMatrix");
				lwgfx.updateMVPUniform("mvpMatrix");
				SelectionFrame sf = getSelectionFrame();
				if (sf.isSelectionPass()) {
					sf.loadID(selectionID, lwgfx);
				} else {
					texture.useTexture(lwgfx);
				}
			}
		};
		if (pane != null) {
			pane.add(this);
		}
	}

	private void move(int x, int y) {
		if (listener != null) {
			listener.onMove(x, y);
		}
	}

	private void selection(CursorSelection select, boolean down) {
		if (listener != null) {
			listener.onSelection(select, down);
		}
	}

	private List<LWJGLDrawableNode> children;
	private LWJGLDrawableLeaf representation;
	private Matrix transformation;
	private Matrix buffer;
	private LWJGLTextureLoader texture;
	private int selectionID;
	private CursorListener listener;
	private LWJGLContentPane parent;

}
