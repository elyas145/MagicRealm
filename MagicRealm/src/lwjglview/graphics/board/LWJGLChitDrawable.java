package lwjglview.graphics.board;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import lwjglview.graphics.LWJGLGraphics;
import lwjglview.graphics.animator.AnimationQueue;
import lwjglview.graphics.shader.ShaderType;
import model.enums.ChitType;
import model.enums.TileName;
import config.GraphicsConfiguration;
import view.graphics.board.ChitDrawable;
import view.graphics.Drawable;
import view.graphics.Graphics;

public class LWJGLChitDrawable extends ChitDrawable {

	public static final ShaderType SHADER = ShaderType.CHIT_SHADER;

	public LWJGLChitDrawable(LWJGLBoardDrawable bd, ChitType chit, Drawable chitBlock,
			int texid) {
		super(bd, chit);
		board = bd;
		representation = chitBlock;
		textureIndex = texid;
		position = BufferUtils.createFloatBuffer(2);
		movements = new AnimationQueue();
		updatePosition();
	}

	public void moveTo(float x, float y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void draw(Graphics gfx) {
		LWJGLGraphics lwgfx = (LWJGLGraphics) gfx;

		lwgfx.resetModelMatrix();
		lwgfx.scaleModel(GraphicsConfiguration.CHIT_SCALE);
		lwgfx.translateModel(position.get(0), position.get(1),
				GraphicsConfiguration.CHIT_HOVER);

		lwgfx.updateModelViewUniform(SHADER, "modelViewMatrix");
		lwgfx.updateMVPUniform(SHADER, "mvpMatrix");
		lwgfx.getShaders().setUniformIntValue(SHADER, "index", textureIndex);

		representation.draw(gfx);
	}

	private void updatePosition() {
		board.getChitPosition(getChitType(), position);
	}
	
	private AnimationQueue movements;

	private LWJGLBoardDrawable board;

	private FloatBuffer position;

	private Drawable representation;

	private int textureIndex;

}
