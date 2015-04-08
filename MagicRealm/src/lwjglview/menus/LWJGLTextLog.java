package lwjglview.menus;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import config.GraphicsConfiguration;
import utils.math.linear.Matrix;
import lwjglview.graphics.LWJGLGraphics;
import lwjglview.graphics.animator.matrixcalculator.StaticMatrixCalculator;

public class LWJGLTextLog extends LWJGLContentPane {

	private static final int TEXT_HEIGHT = 40;
	private static final Font FONT = new Font("Times New Roman", Font.PLAIN,
			TEXT_HEIGHT);
	private static final Color COLOR = Color.CYAN;

	public LWJGLTextLog(LWJGLContentPane pane, float x, float y, float pw,
			float ph, float dx, float dy, int max, float update) {
		super(pane);
		init(x, y, pw, ph, dx, dy, max, FONT, COLOR, update);
	}

	public LWJGLTextLog(LWJGLContentPane pane, float x, float y, float pw,
			float ph, float dx, float dy, int max, Font fnt, Color col,
			float update) {
		super(pane);
		init(x, y, pw, ph, dx, dy, max, fnt, col, update);
	}

	private void init(float x, float y, float pw, float ph, float dx, float dy,
			int max, Font fnt, Color col, float update) {
		translate = Matrix.translation(x, y, 0f);
		setCalculator(new StaticMatrixCalculator(translate));
		height = ph;
		stringHeight = fnt.getSize() * 5 / 4;
		stringWidth = (int) (stringHeight * pw / ph);
		move = Matrix.columnVector(dx, dy, 0f);
		maxPanels = max;
		text = new ArrayList<LWJGLPanel>();
		font = fnt;
		color = col;
		updateTime = update;
		visible = true;
	}

	public synchronized void addText(String txt) {
		LWJGLPanel pane;
		if (text.size() == maxPanels) {
			pane = text.remove(0);
			pane.resetPosition(0f);
			pane.updateFromString(txt, font, color);
		} else {
			pane = LWJGLPanel.fromString(this, txt, font, color, stringWidth,
					stringHeight, 0f, 0f, height, false);
		}
		for (LWJGLPanel panel : text) {
			panel.slide(move, updateTime);
		}
		text.add(pane);
		pane.setVisible(true);
	}

	public void setVisible(boolean vis) {
		visible = vis;
	}

	@Override
	public synchronized void draw(LWJGLGraphics gfx) {
		if(visible) {
			updateTransformation();
			drawPanels(gfx);
		}
	}

	@Override
	public void updateNodeUniforms(LWJGLGraphics gfx) {
	}

	private void drawPanels(LWJGLGraphics gfx) {
		for (LWJGLPanel pane : text) {
			pane.draw(gfx);
		}
	}

	private Matrix translate;
	private float height;
	private Matrix move;
	private int maxPanels;
	private int stringWidth, stringHeight;
	private Font font;
	private Color color;
	private float updateTime;
	private boolean visible;

	private List<LWJGLPanel> text;

}
