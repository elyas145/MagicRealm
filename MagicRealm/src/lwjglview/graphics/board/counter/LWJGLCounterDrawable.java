package lwjglview.graphics.board.counter;

import java.awt.Color;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import lwjglview.graphics.LWJGLDrawableNode;
import lwjglview.graphics.LWJGLGraphics;
import lwjglview.graphics.animator.AnimationQueue;
import lwjglview.graphics.animator.FadeAnimator;
import lwjglview.graphics.animator.MovementAnimator;
import lwjglview.graphics.board.LWJGLBoardDrawable;
import lwjglview.graphics.shader.ShaderType;
import model.enums.CounterType;
import config.GraphicsConfiguration;
import utils.math.Matrix;

public class LWJGLCounterDrawable extends LWJGLDrawableNode {

	public static final ShaderType SHADER = ShaderType.CHIT_SHADER;

	public LWJGLCounterDrawable(LWJGLBoardDrawable bd, CounterType ctr, LWJGLDrawableNode chitBlock,
			int texid) {
		super(bd);
		textureIndex = texid;
		counter = ctr;
		representation = chitBlock;
		textureIndex = texid;
		position = null;
		buffer = BufferUtils.createFloatBuffer(4);
		basis4 = Matrix.columnVector(0f, 0f, 0f, 1f);
		colours = new AnimationQueue();
		colours.start();
		colourBuffer = new float[4];
		currentColour = null;
		changeColour(Color.WHITE);
		movements = new AnimationQueue();
		movements.start();
	}

	public CounterType getCounterType() {
		return counter;
	}

	public void changeColour(Color col) {
		col.getRGBComponents(colourBuffer);
		Matrix newCol = Matrix.columnVector(colourBuffer);
		if (currentColour == null) {
			currentColour = newCol;
		}
		colours.push(new FadeAnimator(GraphicsConfiguration.COUNTER_FLIP_TIME,
				currentColour, newCol));
		currentColour = newCol;
	}

	public void moveTo(float x, float y) {
		buffer.put(0, x);
		buffer.put(1, y);
		move();
	}

	public boolean moving() {
		return !movements.isFinished();
	}

	public Matrix getVector() {
		Matrix transform = getTransform();
		transform = transform.multiply(basis4);
		return Matrix.columnVector(transform.get(0, 0), transform.get(1, 0),
				transform.get(2, 0));
	}

	public Matrix getTransform() {
		if (!movements.isFinished()) {
			return movements.apply();
		}
		return Matrix.translation(position.get(0), position.get(1), 0f);
	}

	public boolean isAnimationFinished() {
		return movements.isFinished() && colours.isFinished();
	}

	@Override
	public void applyNodeTransformation(LWJGLGraphics lwgfx) {
		lwgfx.scaleModel(GraphicsConfiguration.CHIT_SCALE);
		lwgfx.applyModelTransform(getTransform());
		lwgfx.translateModel(0f, 0f, GraphicsConfiguration.CHIT_HOVER
				+ GraphicsConfiguration.TILE_THICKNESS * .5f);
	}

	@Override
	public void updateNodeUniforms(LWJGLGraphics lwgfx) {
		lwgfx.updateModelViewUniform(SHADER, "modelViewMatrix");
		lwgfx.updateMVPUniform(SHADER, "mvpMatrix");
		lwgfx.getShaders().setUniformIntValue("index", textureIndex);
		buffer.rewind();
		colours.apply().toFloatBuffer(buffer);
		lwgfx.getShaders().setUniformFloatArrayValue(SHADER, "counterColour",
				4, buffer);
	}

	@Override
	public void draw(LWJGLGraphics lwgfx) {

		representation.setParent(this);
		representation.draw(lwgfx);
		
	}

	private void move() {
		float xf, yf;
		xf = buffer.get(0);
		yf = buffer.get(1);
		Matrix fin = Matrix.columnVector(xf, yf, 0f);
		if (position == null) {
			position = BufferUtils.createFloatBuffer(2);
		} else {
			Matrix init = Matrix.columnVector(position.get(0), position.get(1),
					0f);
			movements.push(new MovementAnimator(
					GraphicsConfiguration.ANIMATION_SPEED, init, fin) {
				@Override
				public void finish() {
				}
			});
		}
		position.put(0, xf);
		position.put(1, yf);
	}

	private float[] colourBuffer;

	private Matrix currentColour;

	private Matrix basis4;

	private AnimationQueue movements;
	private AnimationQueue colours;

	private FloatBuffer position;
	private FloatBuffer buffer;

	private LWJGLDrawableNode representation;

	private int textureIndex;
	
	private CounterType counter;

}
