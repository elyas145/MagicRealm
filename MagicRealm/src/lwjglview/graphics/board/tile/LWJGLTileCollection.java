package lwjglview.graphics.board.tile;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import utils.images.ImageTools;
import utils.math.Mathf;
import utils.math.linear.Matrix;
import utils.resources.ResourceHandler;
import utils.resources.TileImages;
import view.selection.CursorListener;
import view.selection.CursorSelection;
import config.GraphicsConfiguration;
import lwjglview.graphics.LWJGLDrawableNode;
import lwjglview.graphics.LWJGLGraphics;
import lwjglview.graphics.board.LWJGLBoardDrawable;
import lwjglview.graphics.shader.ShaderType;
import lwjglview.graphics.textures.LWJGLTextureArrayLoader;
import lwjglview.graphics.textures.LWJGLTextureLoader;
import lwjglview.selection.SelectionFrame;
import lwjglview.selection.SelectionShaderType;
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
		selectWidth = 512;
		selectHeight = 444;
		selectionTextures = new LWJGLTextureArrayLoader(selectWidth,
				selectHeight);
		tiles = new HashMap<TileName, LWJGLTileDrawable>();
		textureLocations = new HashMap<TileName, EnchantedHolder<LWJGLTextureLoader>>();
		selectionLocations = new HashMap<TileName, EnchantedHolder<LWJGLTextureLoader>>();
		buffer3 = Matrix.zeroVector(3);
		buffer3A = Matrix.zeroVector(3);
		remaining = new HashSet<TileName>();
		for(TileName tn: TileName.values()) {
			if(tn != TileName.EMPTY) {
				remaining.add(tn);
			}
		}
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
		EnchantedHolder<LWJGLTextureLoader> loc = textureLocations.get(tile);
		selectionLocations.put(tile, new EnchantedHolder<LWJGLTextureLoader>());
		EnchantedHolder<LWJGLTextureLoader> sel = selectionLocations.get(tile);
		ClearingSelectionGenerator selectionGen = new ClearingSelectionGenerator(
				tile, clears);
		selectionGen.setEnchanted(false);
		sel.set(false, selectionTextures.addImage(selectionGen));
		selectionGen.setEnchanted(true);
		sel.set(true, selectionTextures.addImage(selectionGen));
		int row = cl;
		int col = rw;
		float r = Mathf.PI * rot / 3f;
		synchronized(buffer3) {
			buffer3.fill((row % 2 == 0 ? 0f : 1.5f) + col * 3f, -row * 0.866025f,
					0f);
			tiles.put(tile, new LWJGLTileDrawable(this, buffer3, r, selectionGen.getID(), loc, sel,
					clears));
		}
		synchronized(remaining) {
			remaining.remove(tile);
		}
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
		if (isSelectionPass()) {
			getSelectionFrame().useShader(gfx.getShaders(), SelectionShaderType.TILE);
			if(!selectionTextures.loadedTextures()) {
				if(remaining.isEmpty()) {
					selectionTextures.loadImages();
					selectionTextures.loadTextures(gfx, false);
				}
			}
			else {
				selectionTextures.useTextures(gfx);
			}
		} else {
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
							new EnchantedHolder<LWJGLTextureLoader>(textures
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
			if(down) {
				board.requestFocus(tile, clearing);
			}
		}

		private TileName tile;
		private int clearing;

	}

	private class ClearingSelectionGenerator implements
			ImageTools.GraphicsHandler {

		public ClearingSelectionGenerator(TileName tile,
				Iterable<? extends ClearingInterface> clears) {
			SelectionFrame sf = board.getSelectionFrame();
			tileID = sf.getNewID(new ChangeFocus(tile, 0));
			tileName = tile;
			enchanted = false;
			clearings = clears;
		}

		public void setEnchanted(boolean ench) {
			enchanted = ench;
		}
		
		public int getID() {
			return tileID;
		}

		@Override
		public void draw(Graphics g, int width, int height) {
			Graphics2D g2d = (Graphics2D) g;
			SelectionFrame sf = board.getSelectionFrame();
			g.setColor(sf.getColor(tileID));
			g.fillRect(0, 0, selectWidth, selectHeight);
			synchronized (buffer3) {
				for (ClearingInterface cli : clearings) {
					cli.getPosition(enchanted, buffer3);
					// flip position
					buffer3.set(1, 0, -buffer3.get(1, 0) / 0.866025f);
					// move to corner in origin
					buffer3A.fill(1f, 1f, 0f);
					buffer3.add(buffer3A, buffer3);
					// scale up to full size
					buffer3.multiply(.5f, buffer3);
					buffer3.set(0, 0, buffer3.get(0, 0) * width);
					buffer3.set(1, 0, buffer3.get(1, 0) * height);
					int num = cli.getClearingNumber();
					Color c = sf.getColor(sf.getNewID(new ChangeFocus(tileName, num)));
					g.setColor(c);
					float scale = GraphicsConfiguration.CLEARING_RADUS * width;
					// Assume x, y, and diameter are instance variables.
					Ellipse2D.Double circle = new Ellipse2D.Double(buffer3.get(
							0, 0) - scale * .5f, buffer3.get(1, 0) - scale * .5f, scale, scale);
					g2d.fill(circle);
				}
			}
		}
		
		@Override
		public int post(int in) {
			return in;
		}

		private TileName tileName;
		private boolean enchanted;
		private int tileID;
		private Iterable<? extends ClearingInterface> clearings;

	}

	private int selectWidth;
	private int selectHeight;
	
	private Set<TileName> remaining;

	private Map<TileName, EnchantedHolder<LWJGLTextureLoader>> textureLocations;
	private Map<TileName, EnchantedHolder<LWJGLTextureLoader>> selectionLocations;
	private Map<TileName, LWJGLTileDrawable> tiles;
	private LWJGLTextureArrayLoader textures;
	private LWJGLTextureArrayLoader selectionTextures;
	private LWJGLBoardDrawable board;
	private Matrix buffer3;
	private Matrix buffer3A;
}
