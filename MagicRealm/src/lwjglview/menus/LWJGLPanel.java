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
import lwjglview.graphics.animator.AnimationQueue;
import lwjglview.graphics.animator.FadeAnimator;
import lwjglview.graphics.animator.matrixcalculator.MatrixCalculator;
import lwjglview.graphics.textures.LWJGLSingleTextureLoader;
import lwjglview.graphics.textures.LWJGLTextureLoader;
import lwjglview.selection.SelectionFrame;

public class LWJGLPanel extends LWJGLContentPane {

	public enum Type {
		FOREGROUND, BACKGROUND
	}

	public static LWJGLPanel fromTextureLoader(LWJGLContentPane cp,
			LWJGLTextureLoader texture, float x, float y, float size,
			boolean sel) {
		return new LWJGLPanel(cp, texture, x, y, size, sel);
	}

	public static LWJGLPanel fromPicture(LWJGLContentPane cp,
			ResourceHandler rh, String path, float x, float y, float size,
			boolean sel) {
		LWJGLTextureLoader texture = new LWJGLSingleTextureLoader(rh, path);
		return LWJGLPanel.fromTextureLoader(cp, texture, x, y, size, sel);
	}

	public static LWJGLPanel fromGraphics(LWJGLContentPane cp,
			ImageTools.GraphicsHandler gh, int width, int height, float x,
			float y, float size, boolean sel) {
		LWJGLTextureLoader texture = new LWJGLSingleTextureLoader(gh, width,
				height);
		return LWJGLPanel.fromTextureLoader(cp, texture, x, y, size, sel);
	}

	public static LWJGLPanel fromString(LWJGLContentPane cp, String str,
			Font fnt, Color col, int width, int height, float x, float y,
			float size, boolean sel) {
		return LWJGLPanel.fromGraphics(cp, new ImageTools.StringDrawer(str,
				fnt, col), width, height, x, y, size, sel);
	}

	public LWJGLPanel(LWJGLContentPane par, LWJGLTextureLoader text,
			float xPos, float yPos, float scale, boolean sel) {
		super(par);
		float w, h;
		w = text.getWidth();
		h = text.getHeight();
		init(par, par.getSelectionFrame(), text, xPos, yPos, scale * w / h,
				scale, sel);
	}

	public LWJGLPanel(LWJGLContentPane par, LWJGLTextureLoader text,
			float xPos, float yPos, float w, float h, boolean sel) {
		super(par);
		init(par, par.getSelectionFrame(), text, xPos, yPos, w, h, sel);
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

	public void add(LWJGLContentPane other, Type tp) {
		switch (tp) {
		case BACKGROUND:
			synchronized (bgChildren) {
				bgChildren.add(other);
			}
			break;
		case FOREGROUND:
			synchronized (fgChildren) {
				fgChildren.add(other);
			}
			break;
		}
		other.setParent(this);
	}

	@Override
	public void add(LWJGLContentPane other) {
		add(other, Type.FOREGROUND);
	}

	public void remove(LWJGLContentPane other, Type tp) {
		switch (tp) {
		case BACKGROUND:
			synchronized (bgChildren) {
				bgChildren.remove(other);
			}
			break;
		case FOREGROUND:
			synchronized (fgChildren) {
				fgChildren.remove(other);
			}
			break;
		}
	}

	@Override
	public void remove(LWJGLContentPane other) {
		remove(other, Type.FOREGROUND);
		remove(other, Type.BACKGROUND);
	}

	@Override
	public SelectionFrame getSelectionFrame() {
		return parent.getSelectionFrame();
	}

	@Override
	public void draw(LWJGLGraphics gfx) {
		if (visible) {
			updateTransformation();

			drawBgChildren(gfx);

			SelectionFrame sf = getSelectionFrame();

			if (sf.isSelectionPass()) {
				if (selectable) {
					sf.loadID(selectionID, gfx);
					representation.draw(gfx);
				}
			} else {
				texture.useTexture(gfx);
				representation.draw(gfx);
			}

			drawFgChildren(gfx);
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
		synchronized (vec3) {
			currentPosition.add(amt, vec3);
			moveTo(vec3, time);
		}
	}

	public void resetPosition() {
		moveTo(rootPosition, GraphicsConfiguration.PANEL_TIME);
	}

	public LWJGLTextureLoader getTexture() {
		return texture;
	}
	
	public void updateFromString(String message, Font font, Color color) {
		updateFromGraphics(new ImageTools.StringDrawer(message,
				font, color));
	}
	
	public void updateFromGraphics(ImageTools.GraphicsHandler gh) {
		getTexture().updateFromGraphicsHandler(gh);
	}

	private void init(LWJGLContentPane pane, SelectionFrame select,
			LWJGLTextureLoader text, float xPos, float yPos, float w, float h,
			boolean sel) {
		selectable = sel;
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
		fgChildren = new ArrayList<LWJGLDrawableNode>();
		bgChildren = new ArrayList<LWJGLDrawableNode>();
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
		visible = false;
	}

	private void drawFgChildren(LWJGLGraphics gfx) {
		synchronized (fgChildren) {
			for (LWJGLDrawableNode child : fgChildren) {
				child.draw(gfx);
			}
		}
	}

	private void drawBgChildren(LWJGLGraphics gfx) {
		synchronized (bgChildren) {
			for (LWJGLDrawableNode child : bgChildren) {
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

	private List<LWJGLDrawableNode> bgChildren;
	private List<LWJGLDrawableNode> fgChildren;
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
	private float width;
	private float height;

}
