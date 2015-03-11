package lwjglview.menus;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import config.GraphicsConfiguration;
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
import lwjglview.graphics.animator.AnimationQueue;
import lwjglview.graphics.animator.FadeAnimator;
import lwjglview.graphics.animator.matrixcalculator.MatrixCalculator;
import lwjglview.selection.SelectionFrame;

public class LWJGLPanel extends LWJGLContentPane {
	
	public enum Type {
		FOREGROUND,
		BACKGROUND
	}

	public static LWJGLPanel fromTextureLoader(LWJGLContentPane cp,
			LWJGLTextureLoader texture, float x, float y, float size,
			Type tp, boolean sel) {
		return new LWJGLPanel(cp, texture, x, y, size, tp, sel);
	}

	public static LWJGLPanel fromPicture(LWJGLContentPane cp,
			ResourceHandler rh, String path, float x, float y, float size,
			Type tp, boolean sel) {
		LWJGLTextureLoader texture = new LWJGLTextureLoader(rh, path);
		return LWJGLPanel.fromTextureLoader(cp, texture, x, y, size, tp, sel);
	}

	public static LWJGLPanel fromGraphics(LWJGLContentPane cp,
			ImageTools.GraphicsHandler gh, int width, int height, float x,
			float y, float size, Type tp, boolean sel) {
		LWJGLTextureLoader texture = new LWJGLTextureLoader(gh, width, height);
		return LWJGLPanel.fromTextureLoader(cp, texture, x, y, size, tp, sel);
	}

	public static LWJGLPanel fromString(LWJGLContentPane cp, String str,
			Font fnt, Color col, int width, int height, float x, float y,
			float size, Type tp, boolean sel) {
		return LWJGLPanel.fromGraphics(cp, new ImageTools.StringDrawer(str,
				fnt, col), width, height, x, y, size, tp, sel);
	}

	public LWJGLPanel(LWJGLContentPane par, LWJGLTextureLoader text,
			float xPos, float yPos, float scale, Type tp, boolean sel) {
		super(par);
		float w, h;
		w = text.getWidth();
		h = text.getHeight();
		init(par, par.getSelectionFrame(), text, xPos, yPos, scale * w / h, scale, tp, sel);
	}

	public LWJGLPanel(LWJGLContentPane par, LWJGLTextureLoader text,
			float xPos, float yPos, float w, float h, Type tp, boolean sel) {
		super(par);
		init(par, par.getSelectionFrame(), text, xPos, yPos, w, h, tp, sel);
	}

	public void setCursorListener(CursorListener cl) {
		listener = cl;
	}

	public void setVisible(boolean b) {
		visible = b;
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}

	@Override
	public void add(LWJGLPanel other) {
		synchronized(children) {
			children.add(other);
		}
		other.setParent(this);
	}

	@Override
	public SelectionFrame getSelectionFrame() {
		return parent.getSelectionFrame();
	}

	@Override
	public void draw(LWJGLGraphics gfx) {
		if (visible) {
			updateTransformation();
			
			if(type == Type.BACKGROUND) { // draw children first
				drawChildren(gfx);
			}

			SelectionFrame sf = getSelectionFrame();

			if (sf.isSelectionPass()) {
				if (selectable) {
					sf.loadID(selectionID, gfx);
					representation.draw(gfx);
				}
			} else {
				texture.useTexture(gfx, 0);
				representation.draw(gfx);
			}

			if(type == Type.FOREGROUND) { // draw children last
				drawChildren(gfx);
			}
		}
	}

	@Override
	public void updateNodeUniforms(LWJGLGraphics gfx) {
		gfx.updateModelViewUniform("modelViewMatrix");
	}

	public void moveTo(Matrix pos, float time) {
		animation.push(new FadeAnimator(time, currentPosition, pos) {
			@Override
			public Matrix calculateTransform() {
				bufferB.translate(super.calculateTransform());
				return bufferB;
			}
		});
		currentPosition.copyFrom(pos);
	}
	
	public void slide(Matrix amt, float time) {
		synchronized(vec3) {
			currentPosition.add(amt, vec3);
			moveTo(vec3, time);
		}
	}

	public void resetPosition() {
		moveTo(rootPosition, GraphicsConfiguration.PANEL_TIME);
	}

	private void init(LWJGLContentPane pane, SelectionFrame select,
			LWJGLTextureLoader text, float xPos, float yPos, float w, float h,
			Type tp, boolean sel) {
		selectable = sel;
		type = tp;
		width = w;
		height = h;
		parent = pane;
		bufferA = Matrix.translation(1f, 1f, 0f);
		vec3 = Matrix.zeroVector(3);
		transformation = Matrix.dilation(w * .5f, h * .5f, 1f, 1f);
		transformation.multiply(bufferA, bufferA);
		rootPosition = Matrix.columnVector(xPos, yPos, 0f);
		currentPosition = Matrix.clone(rootPosition);
		animation = new AnimationQueue();
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
		setCalculator(new MatrixCalculator() {

			@Override
			public Matrix calculateMatrix() {
				return animation.apply();
			}

		});
		children = new ArrayList<LWJGLDrawableNode>();
		representation = new LWJGLDrawableLeaf(this, bufferA,
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
		bufferB = Matrix.identity(4);
		resetPosition();
		animation.start();
		if (pane != null) {
			pane.add(this);
		}
		visible = false;
	}
	
	private void drawChildren(LWJGLGraphics gfx) {
		synchronized(children) {
			for (LWJGLDrawableNode child : children) {
				child.draw(gfx);
			}
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
	private AnimationQueue animation;
	private Matrix transformation;
	private Matrix currentPosition;
	private Matrix rootPosition;
	private Matrix bufferA;
	private Matrix bufferB;
	private Matrix vec3;
	private LWJGLTextureLoader texture;
	private int selectionID;
	private CursorListener listener;
	private LWJGLContentPane parent;
	private boolean visible;
	private boolean selectable;
	private Type type;
	private float width;
	private float height;

}
