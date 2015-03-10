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
import lwjglview.graphics.shader.ShaderType;
import model.board.Board;
import model.enums.TileName;
import model.interfaces.HexTileInterface;

public class SelectionFrame {
	static LWJGLBoardDrawable board;
	static LWJGLGraphics graphics;

	public static void main(String[] args) {
		try {
			ResourceHandler rh = new ResourceHandler();
			graphics = new LWJGLGraphics(rh);
			graphics.clearLayerDepth(GraphicsConfiguration.DISPLAY_START_LAYER);
			graphics.clearLayerDepth(GraphicsConfiguration.SELECTION_START_LAYER);
			graphics.start();
			SelectionFrame selectFrame = new SelectionFrame(graphics);
			board = new LWJGLBoardDrawable(rh, graphics, selectFrame);
			board.setDefaultClearingFocus();
			Board mBoard = new Board(rh);
			for (TileName tn : mBoard.getAllTiles()) {
				HexTileInterface hti = mBoard.getTile(tn);
				board.setTile(tn, hti.getBoardRow(), hti.getBoardColumn(),
						hti.getRotation(), hti.getClearings());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public SelectionFrame(LWJGLGraphics gfx) {
		numIDs = 0;
		listeners = new HashMap<Integer, CursorListener>();
		buffer = BufferUtils.createByteBuffer(4);
		fBuffer = BufferUtils.createFloatBuffer(4);
		queued = new LinkedQueue<CursorListenerInvoker>();
		selectionPass = false;
		frameBufferID = -1;
		textureBufferID = -1;
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
					textureBufferID = gfx.generateBufferTexture();
					gfx.bindFrameBufferTexture(frameBufferID, textureBufferID);
				}
				gfx.useFrameBuffer(frameBufferID);
				gfx.setClearColor(1f, 0f, 0f, 1f);
				gfx.clearActiveBuffers();
				selectionPass = true;
				gfx.getShaders().useShaderProgram(
						ShaderType.SELECTION_SHADER);
			}

		}, GraphicsConfiguration.SELECTION_START_LAYER);
		gfx.prepareLayer(new Handler<LWJGLGraphics>() {

			@Override
			public void handle(LWJGLGraphics gfx) {
				selectionPass = false;
				synchronized(queued) {
					while (!queued.isEmpty()) {
						queued.pop().invoke(gfx);
					}
				}
				gfx.releaseFrameBuffer();
			}

		}, GraphicsConfiguration.SELECTION_END_LAYER);
		gfx.prepareLayer(new Handler<LWJGLGraphics>() {

			@Override
			public void handle(LWJGLGraphics gfx) {
				//gfx.bindTexture(textureBufferID);
				//gfx.clearActiveBuffers();
				//gfx.getShaders().useShaderProgram(ShaderType.BACKGROUND_SHADER);
				//gfx.getPrimitiveTool().drawSquare();
			}
			
		}, LWJGLGraphics.LAYER9);
	}

	public int getNewID(CursorListener listen) {
		int id = ++numIDs;
		listeners.put(id, listen);
		return id;
	}

	public Color getColor(int id) {
		return new Color(getR(id), getG(id), getB(id), getA(id));
	}

	public void loadID(int id, LWJGLGraphics gfx) {
		synchronized (buffer) {
			fBuffer.put(0, getR(id));
			fBuffer.put(1, getG(id));
			fBuffer.put(2, getB(id));
			fBuffer.put(3, getA(id));
			gfx.getShaders().setUniformFloatArrayValue("color", 4, fBuffer);
		}
	}

	public boolean isSelectionPass() {
		return selectionPass;
	}

	private float getR(int id) {
		return maskShift(id, 0xFF000000, 6);
	}

	private float getG(int id) {
		return maskShift(id, 0x00FF0000, 4);
	}

	private float getB(int id) {
		return maskShift(id, 0x0000FF00, 2);
	}

	private float getA(int id) {
		return (id & 0xFF) / 255f;
	}

	private float maskShift(int id, int mask, int shift) {
		mask = (id & mask) >> shift;
		return mask / 255f;
	}

	private int join() {
		int col = 0;
		int mask = 0xFF;
		for (int i = 0; i < 4; ++i) {
			col <<= 2;
			col |= buffer.get(i) & mask;
		}
		return col;
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
			System.out.println(id);
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
	private int textureBufferID;

	private ByteBuffer buffer;
	private FloatBuffer fBuffer;
	private Queue<CursorListenerInvoker> queued;
	private int xPosition;
	private int yPosition;
	private int numIDs;
	private Map<Integer, CursorListener> listeners;
	private boolean selectionPass;
}