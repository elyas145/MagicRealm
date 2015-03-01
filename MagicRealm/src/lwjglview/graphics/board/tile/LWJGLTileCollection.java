package lwjglview.graphics.board.tile;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import utils.math.Mathf;
import utils.math.linear.Matrix;
import utils.resources.ResourceHandler;
import utils.resources.TileImages;
import view.selection.CursorListener;
import view.selection.CursorSelection;
import config.GraphicsConfiguration;
import lwjglview.graphics.LWJGLDrawableNode;
import lwjglview.graphics.LWJGLGraphics;
import lwjglview.graphics.LWJGLTextureArrayLoader;
import lwjglview.graphics.board.LWJGLBoardDrawable;
import lwjglview.graphics.shader.ShaderType;
import lwjglview.selection.SelectionFrame;
import model.EnchantedHolder;
import model.enums.TileName;
import model.interfaces.ClearingInterface;

public class LWJGLTileCollection extends LWJGLDrawableNode {

	public LWJGLTileCollection(LWJGLBoardDrawable par) throws IOException {
		super(par);
		board = par;
		textures = new LWJGLTextureArrayLoader(
				GraphicsConfiguration.IMAGE_SCALE_WIDTH,
				GraphicsConfiguration.IMAGE_SCALE_HEIGHT);
		tiles = new HashMap<TileName, LWJGLTileDrawable>();
		textureLocations = new HashMap<TileName, EnchantedHolder<Integer>>();
		buffer3 = Matrix.zeroVector(3);
		loadTileImages();
	}

	public boolean isEnchanted(TileName name) {
		return get(name).isEnchanted();
	}

	public void getPosition(TileName name, Matrix position) {
		get(name).getPosition(position);
	}

	public Matrix getVector(TileName tile) {
		return get(tile).getVector();
	}

	public void setEnchanted(TileName tile, boolean ench) {
		get(tile).setEnchanted(ench);
	}

	public void setTile(TileName tile, int rw, int cl, int rot,
			Iterable<? extends ClearingInterface> clears) {
		int row = cl;
		int col = rw;
		buffer3.fill((row % 2 == 0 ? 0f : 1.5f) + col * 3f, -row * 0.866025f,
				0f);
		float r = Mathf.PI * rot / 3f;
		EnchantedHolder<Integer> loc = textureLocations.get(tile);
		Map<Integer, Integer> listeners = new HashMap<Integer, Integer>();
		SelectionFrame sf = board.getSelectionFrame();
		listeners.put(0, sf.getNewID(new ChangeFocus(tile, 0)));
		for(ClearingInterface cli: clears) {
			int num = cli.getClearingNumber();
			listeners.put(num, 0);// TODO uncomment sf.getNewID(new ChangeFocus(tile, num)));
		}
		tiles.put(tile, new LWJGLTileDrawable(this, buffer3, r, loc.get(false),
				loc.get(true), clears, listeners));
	}

	public void relocateChit(int id, Matrix pos) {
		board.relocateChit(id, pos);
	}

	public LWJGLTileDrawable get(TileName tl) {
		return tiles.get(tl);
	}
	
	public boolean isSelectionPass() {
		return getSelectionFrame().isSelectionPass();
	}
	
	public SelectionFrame getSelectionFrame() {
		return board.getSelectionFrame();
	}

	@Override
	public void updateNodeUniforms(LWJGLGraphics gfx) {
	}

	@Override
	public void draw(LWJGLGraphics gfx) {
		updateTransformation();
		if(!isSelectionPass()) {
			gfx.getShaders().useShaderProgram(ShaderType.TILE_SHADER);
			textures.useTextures(gfx);
		}
		// draw all tiles
		for (LWJGLTileDrawable tile : tiles.values()) {
			tile.draw(gfx);
		}
	}

	private void loadTileImages() throws IOException {
		ResourceHandler rh = board.getResourceHandler();
		for (TileName type : TileName.values()) {
			textureLocations
					.put(type,
							new EnchantedHolder<Integer>(textures
									.addImage(TileImages.getTileImage(rh, type,
											false)), textures
									.addImage(TileImages.getTileImage(rh, type,
											true))));
		}
		textures.loadImages();
	}
	
	private class ChangeFocus implements CursorListener {
		
		public ChangeFocus(TileName tn, int clr) {
			tile = tn;
			clearing = clr;
		}

		@Override
		public void onMove(int x, int y) {
		}

		@Override
		public void onSelection(CursorSelection select, boolean down) {
			board.requestFocus(tile, clearing);
		}
		
		private TileName tile;
		private int clearing;
		
	}

	private Map<TileName, EnchantedHolder<Integer>> textureLocations;
	private Map<TileName, LWJGLTileDrawable> tiles;
	private LWJGLTextureArrayLoader textures;
	private LWJGLBoardDrawable board;
	private Matrix buffer3;
}
