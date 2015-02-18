package lwjglview.graphics.board;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import lwjglview.graphics.LWJGLGraphics;
import lwjglview.graphics.animator.AnimationQueue;
import lwjglview.graphics.animator.MovementAnimator;
import lwjglview.graphics.shader.ShaderType;
import model.enums.CounterType;
import model.enums.TileName;
import model.interfaces.ClearingInterface;
import model.interfaces.HexTileInterface;
import config.GraphicsConfiguration;
import utils.random.Random;
import view.graphics.board.CounterDrawable;
import view.graphics.Drawable;
import view.graphics.Graphics;

public class LWJGLCounterDrawable extends CounterDrawable {

	public static final ShaderType SHADER = ShaderType.CHIT_SHADER;

	public LWJGLCounterDrawable(LWJGLBoardDrawable bd, CounterType chit,
			Drawable chitBlock, int texid, ClearingInterface loc) {
		super(bd, chit);
		board = bd;
		representation = chitBlock;
		textureIndex = texid;
		position = BufferUtils.createFloatBuffer(2);
		buffer = BufferUtils.createFloatBuffer(2);
		movements = new AnimationQueue();
		movements.start();
		current = loc;
	}

	public void setCurrentClearing(ClearingInterface clear) {
		current = clear;
		updatePosition();
	}

	public void moveTo(float x, float y) {
		buffer.put(0, x);
		buffer.put(1, y);
		move();
	}

	@Override
	public void draw(Graphics gfx) {
		LWJGLGraphics lwgfx = (LWJGLGraphics) gfx;

		lwgfx.resetModelMatrix();
		lwgfx.scaleModel(GraphicsConfiguration.CHIT_SCALE);
		if (!movements.isFinished()) {
			lwgfx.applyModelTransform(movements.apply());
		} else {
			lwgfx.translateModel(position.get(0), position.get(1), 0f);
		}
		lwgfx.translateModel(0f, 0f, GraphicsConfiguration.CHIT_HOVER
				+ GraphicsConfiguration.TILE_THICKNESS * .5f);

		lwgfx.updateModelViewUniform(SHADER, "modelViewMatrix");
		lwgfx.updateMVPUniform(SHADER, "mvpMatrix");
		lwgfx.getShaders().setUniformIntValue(SHADER, "index", textureIndex);

		representation.draw(gfx);
	}

	private void updatePosition() {
		board.setCounter(getCounterType(), current.getParentTile().getName(),
				current.getClearingNumber());
		move();
	}

	private void move() {
		float xo, yo, xf, yf;
		xo = position.get(0);
		yo = position.get(1);
		xf = buffer.get(0);
		yf = buffer.get(1);
		movements.push(new MovementAnimator(1f, xo, yo, xf, yf) {
			@Override
			public void finish() {
				setCurrentClearing(current.getRandomConnection());
			}
		});
		position.put(0, xf);
		position.put(1, yf);
	}

	private ClearingInterface current;

	private AnimationQueue movements;

	private LWJGLBoardDrawable board;

	private FloatBuffer position;
	private FloatBuffer buffer;

	private Drawable representation;

	private int textureIndex;

}
