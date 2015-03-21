package lwjglview.selection;

import java.awt.Color;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.BufferUtils;

import config.GraphicsConfiguration;
import utils.handler.Handler;
import utils.resources.ResourceHandler;
import utils.structures.LinkedQueue;
import utils.structures.Queue;
import view.selection.CursorListener;
import view.selection.CursorSelection;
import lwjglview.graphics.LWJGLGraphics;
import lwjglview.graphics.board.LWJGLBoardDrawable;
import lwjglview.graphics.shader.GLShaderHandler;
import lwjglview.graphics.shader.ShaderType;
import model.board.Board;
import model.enums.TileName;
import model.interfaces.HexTileInterface;

public class SelectionFrame {
	
	public SelectionFrame(LWJGLGraphics gfx) {
		numIDs = 0;
		listeners = new HashMap<Integer, CursorListener>();
		buffer = BufferUtils.createByteBuffer(4);
		fBuffer = BufferUtils.createFloatBuffer(4);
		queued = new LinkedQueue<CursorListenerInvoker>();
		selectionPass = false;
		frameBufferID = -1;
		gfx.setCursorListener(new CursorListener() {

			@Override
			public void onMove(int x, int y) {
				xPosition = x;
				yPosition = y;
				queued.push(new MovementInvoker(x, y));
			}

			@Override
			public void onSelection(CursorSelection select, boolean down) {
				queued.push(new ClickInvoker(xPosition, yPosition, select, down));
			}

		});
		gfx.prepareLayer(new Handler<LWJGLGraphics>() {

			@Override
			public void handle(LWJGLGraphics gfx) {
				if (frameBufferID < 0) {
					frameBufferID = gfx.createFrameBuffer();
					gfx.generateBufferTexture(frameBufferID);
				}
				gfx.useFrameBuffer(frameBufferID);
				gfx.setClearColor(0f, 0f, 0f, 0f);
				gfx.clearActiveBuffers();
				selectionPass = true;
				useShader(gfx.getShaders(), SelectionShaderType.PLAIN);
			}

		}, GraphicsConfiguration.SELECTION_START_LAYER);
		gfx.finishLayer(new Handler<LWJGLGraphics>() {

			@Override
			public void handle(LWJGLGraphics gfx) {
				selectionPass = false;
				synchronized (queued) {
					while (!queued.isEmpty()) {
						queued.pop().invoke(gfx);
					}
				}
				gfx.releaseFrameBuffer();
			}

		}, GraphicsConfiguration.SELECTION_END_LAYER);
	}

	static final int incr = 1;

	public int getNewID(CursorListener listen) {
		int id = ++numIDs;
		listeners.put(id, listen);
		return id;
	}

	public Color getColor(int id) {
		id *= incr;
		return new Color(getR(id), getG(id), getB(id));
	}

	public void loadID(int id, LWJGLGraphics gfx) {
		loadID("color", id, gfx);
	}
	
	public void loadID(String uniform, int id, LWJGLGraphics gfx) {
		loadID(id, gfx, uniform);
	}

	public void loadID(int id, LWJGLGraphics gfx, String name) {
		id *= incr;
		synchronized (fBuffer) {
			fBuffer.put(0, getR(id));
			fBuffer.put(1, getG(id));
			fBuffer.put(2, getB(id));
			fBuffer.put(3, 1f);
			gfx.getShaders().setUniformFloatArrayValue("color", 4, fBuffer);
		}
	}
	
	public void useShader(GLShaderHandler shaders, SelectionShaderType shade) {
		switch(shade) {
		case TILE:
			shaders.useShaderProgram(ShaderType.TILE_SELECTION_SHADER);
			break;
		case MENU:
			shaders.useShaderProgram(ShaderType.MENU_SELECTION_SHADER);
			break;
		default:
			shaders.useShaderProgram(ShaderType.SELECTION_SHADER);
			break;
		}
	}

	public boolean isSelectionPass() {
		return selectionPass;
	}

	private int getRi(int id) {
		return maskShift(id, 0);
	}

	private int getGi(int id) {
		return maskShift(id, 1);
	}

	private int getBi(int id) {
		return maskShift(id, 2);
	}

	private float getR(int id) {
		return getRi(id) / 255f;
	}

	private float getG(int id) {
		return getGi(id) / 255f;
	}

	private float getB(int id) {
		return getBi(id) / 255f;
	}

	private int maskShift(int id, int shift) {
		return (id >> (shift * 8)) & 0xFF;
	}

	private int join() {
		int col = 0;
		for (int i = 2; i >=0; --i) {
			col <<= 8;
			col |= buffer.get(i) & 0xFF;
		}
		return (col + incr - 1) / incr;
	}

	private abstract class CursorListenerInvoker {
		public CursorListenerInvoker(int x, int y) {
			xpos = x;
			ypos = y;
		}

		public void invoke(LWJGLGraphics gfx) {
			int id;
			synchronized (buffer) {
				gfx.getPixel(xpos, ypos, buffer);
				id = join();
			}
			CursorListener listener = listeners.get(id);
			if (listener != null) {
				invoke(listener);
			}
		}

		public abstract void invoke(CursorListener cl);

		int xpos, ypos;
	}

	private class MovementInvoker extends CursorListenerInvoker {

		public MovementInvoker(int x, int y) {
			super(x, y);
			xpos = x;
			ypos = y;
		}

		@Override
		public void invoke(CursorListener cl) {
			cl.onMove(xpos, ypos);
		}

		int xpos, ypos;

	}

	private class ClickInvoker extends CursorListenerInvoker {

		public ClickInvoker(int x, int y, CursorSelection type, boolean dn) {
			super(x, y);
			selection = type;
			down = dn;
		}

		@Override
		public void invoke(CursorListener cl) {
			cl.onSelection(selection, down);
		}

		CursorSelection selection;
		boolean down;

	}

	private int frameBufferID;

	private ByteBuffer buffer;
	private FloatBuffer fBuffer;
	private Queue<CursorListenerInvoker> queued;
	private int xPosition;
	private int yPosition;
	private int numIDs;
	private Map<Integer, CursorListener> listeners;
	private boolean selectionPass;
}
