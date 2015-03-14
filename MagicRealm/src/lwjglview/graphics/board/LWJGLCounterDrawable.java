package lwjglview.graphics.board;

import java.awt.Color;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import lwjglview.graphics.LWJGLDrawableNode;
import lwjglview.graphics.LWJGLGraphics;
import lwjglview.graphics.animator.AnimationQueue;
import lwjglview.graphics.animator.FadeAnimator;
import lwjglview.graphics.animator.MovementAnimator;
import lwjglview.graphics.animator.matrixcalculator.MatrixCalculator;
import lwjglview.graphics.shader.ShaderType;
import model.enums.CounterType;
import config.GraphicsConfiguration;
import utils.math.linear.Matrix;

public class LWJGLCounterDrawable extends LWJGLDrawableNode implements
		MatrixCalculator {

	public static final ShaderType SHADER = ShaderType.CHIT_SHADER;

	public LWJGLCounterDrawable(LWJGLBoardDrawable bd,
			LWJGLDrawableNode chitBlock, int texid, Color col) {
		super(bd);
		board = bd;
		textureIndex = texid;
		identifier = board.addCounterDrawable(this);
		representation = chitBlock;
		textureIndex = texid;
		position = null;
		vec3 = Matrix.empty(3, 1);
		buffer = BufferUtils.createFloatBuffer(4);
		basis4 = Matrix.columnVector(0f, 0f, 0f, 1f);
		colours = new AnimationQueue();
		colours.start();
		colourBuffer = new float[4];
		currentColour = null;
		buffer4 = Matrix.empty(4, 1);
		buffer4A = Matrix.empty(4, 1);
		changeColour(col);
		float cs = GraphicsConfiguration.CHIT_SCALE;
		scale = Matrix.dilation(cs, cs, cs, 1f);
		hover = Matrix.translation(0f, 0f, GraphicsConfiguration.CHIT_HOVER
				+ GraphicsConfiguration.TILE_THICKNESS * .5f);
		bufferMatrix = Matrix.identity(4);
		movements = new AnimationQueue();
		movements.start();
		setCalculator(this);
	}

	public int getID() {
		return identifier;
	}

	public void forget() {
		board.removeCounterDrawable(getID());
	}

	public synchronized void changeColour(Color col) {
		col.getRGBComponents(colourBuffer);
		buffer4.fill(colourBuffer);
		if (currentColour == null) {
			currentColour = Matrix.clone(buffer4);
		}
		currentColour.subtract(buffer4, buffer4A);
		float dist = buffer4A.length();
		colours.push(new FadeAnimator(dist
				* GraphicsConfiguration.COUNTER_FLIP_TIME, currentColour,
				buffer4));
		currentColour.copyFrom(buffer4);
	}

	public void moveTo(Matrix pos) {
		synchronized(vec3) {
			vec3.copyFrom(pos);
			move();
		}
	}

	public boolean moving() {
		return !movements.isFinished();
	}

	public void getVector(Matrix focus) {
		Matrix transform = getTransform();
		transform.multiply(basis4, focus);
	}

	public Matrix getTransform() {
		if (!movements.isFinished()) {
			return movements.apply();
		}
		bufferMatrix.translate(position);
		return bufferMatrix;
	}

	public boolean isAnimationFinished() {
		return movements.isFinished() && colours.isFinished();
	}

	public LWJGLDrawableNode getRepresentation() {
		return representation;
	}

	@Override
	public void updateNodeUniforms(LWJGLGraphics lwgfx) {
		lwgfx.updateModelViewUniform("modelViewMatrix");
		lwgfx.updateMVPUniform("mvpMatrix");
		lwgfx.getShaders().setUniformIntValue("index", textureIndex);
		buffer.rewind();
		colours.apply().toFloatBuffer(buffer);
		lwgfx.getShaders().setUniformFloatArrayValue("counterColour",
				4, buffer);
	}

	@Override
	public void draw(LWJGLGraphics lwgfx) {
		updateTransformation();
		
		representation.setParent(this);
		representation.draw(lwgfx);

	}

	@Override
	public Matrix calculateMatrix() {
		getTransform().multiply(hover, bufferMatrix);
		bufferMatrix.multiply(scale, bufferMatrix);
		return bufferMatrix;
	}

	private void move() {
		System.out.println(vec3);
		if(vec3.get(2, 0) > 0f) {
			throw new RuntimeException(this.toString());
		}
		if (position == null) {
			position = Matrix.zeroVector(3);
		} else {
			movements.push(new MovementAnimator(
					GraphicsConfiguration.ANIMATION_SPEED, position, vec3) {
				@Override
				public void finish() {
				}
			});
		}
		position.copyFrom(vec3);
	}

	private LWJGLBoardDrawable board;

	private float[] colourBuffer;

	private Matrix currentColour;
	
	private Matrix buffer4;
	private Matrix buffer4A;
	private Matrix basis4;

	private Matrix scale;
	private Matrix hover;
	private Matrix bufferMatrix;

	private AnimationQueue movements;
	private AnimationQueue colours;

	private FloatBuffer buffer;
	
	private Matrix position;
	private Matrix vec3;

	private LWJGLDrawableNode representation;

	private int textureIndex;

	private int identifier;

}
