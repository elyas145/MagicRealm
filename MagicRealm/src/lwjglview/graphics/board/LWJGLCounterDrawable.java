package lwjglview.graphics.board;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import lwjglview.graphics.LWJGLGraphics;
import lwjglview.graphics.animator.AnimationQueue;
import lwjglview.graphics.animator.MovementAnimator;
import lwjglview.graphics.shader.ShaderType;
import model.enums.CounterType;
import config.GraphicsConfiguration;
import view.graphics.board.CounterDrawable;
import view.graphics.Drawable;
import view.graphics.Graphics;

public class LWJGLCounterDrawable extends CounterDrawable {

	public static final ShaderType SHADER = ShaderType.CHIT_SHADER;

	public LWJGLCounterDrawable(LWJGLBoardDrawable bd, CounterType chit,
			Drawable chitBlock, int texid) {
		super(bd, chit);
		board = bd;
		representation = chitBlock;
		textureIndex = texid;
		position = null;
		buffer = BufferUtils.createFloatBuffer(2);
		movements = new AnimationQueue();
		movements.start();
	}

	public void moveTo(float x, float y) {
		buffer.put(0, x);
		buffer.put(1, y);
		move();
	}

	public boolean moving() {
		return !movements.isFinished();
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

	private void move() {
		float xo, yo, xf, yf;
		xf = buffer.get(0);
		yf = buffer.get(1);
		if(position == null) {
			position = BufferUtils.createFloatBuffer(2);
		}
		else {
			xo = position.get(0);
			yo = position.get(1);
			movements.push(new MovementAnimator(GraphicsConfiguration.ANIMATION_SPEED, xo, yo, xf, yf) {
				@Override
				public void finish() { }
			});
		}
		position.put(0, xf);
		position.put(1, yf);
	}

	private AnimationQueue movements;

	private LWJGLBoardDrawable board;

	private FloatBuffer position;
	private FloatBuffer buffer;

	private Drawable representation;

	private int textureIndex;

}
