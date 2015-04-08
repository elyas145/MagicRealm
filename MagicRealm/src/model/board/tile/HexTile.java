package model.board.tile;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.EnchantedHolder;
import model.board.Board;
import model.board.Board.ClearingData;
import model.board.clearing.Clearing;
import model.counter.chit.Chit;
import model.enums.PathType;
import model.enums.TileName;
import model.interfaces.HexTileInterface;

import org.json.simple.JSONArray;

import communication.handler.server.serialized.SerializedClearing;
import communication.handler.server.serialized.SerializedTile;
import utils.math.Mathf;
import utils.math.Point;
import utils.math.linear.Matrix;
import utils.tools.IterationTools;

public class HexTile implements HexTileInterface {

	private static final long serialVersionUID = 5303629252128539756L;
	private boolean enchanted = false;
	public HexTile(Board par, TileName tile, int x, int y, int rot,
			Map<Integer, EnchantedHolder<ClearingData>> locations,
			TileName[] surrounding) {
		parent = par;
		name = tile;
		row = x;
		column = y;
		rotation = rot;
		clearings = new HashMap<Integer, Clearing>();
		bufferA = Matrix.zeroVector(3);
		bufferB = Matrix.zeroVector(3);
		bufferC = Matrix.square(3);
		for (Map.Entry<Integer, EnchantedHolder<ClearingData>> ents : locations
				.entrySet()) {
			EnchantedHolder<ClearingData> pts = ents.getValue();
			int num = ents.getKey();
			if (!pts.has(false)) {
				throw new RuntimeException(
						"Missing normal position data for tile " + name
								+ ", clearing " + num);
			}
			if (!pts.has(true)) {
				throw new RuntimeException(
						"Missing enchanted position data for tile " + name
								+ ", clearing " + num);
			}
			Clearing cl = new Clearing(this, num, transform(
					pts.get(false).point, bufferA), transform(
					pts.get(true).point, bufferB), pts.get(false).type);
			clearings.put(num, cl);
		}
		exits = new int[2][6];
		surroundings = new TileName[6];
		for(int i = 0; i < 6; ++i) {
			surroundings[i] = surrounding[i];
		}
		clearExits();
		connectClearings();
		for (int i = 0; i < surrounding.length; ++i) {
			HexTile other = getParent().getTile(surrounding[i]);
			if (other != null) {
				int entr = (i + 3) % 6;
				Clearing ocl = other.getEntryClearing(entr, false);
				Clearing tcl = getEntryClearing(i, false);
				if (ocl != null && tcl != null) {
					ocl.connectTo(this, i, false);
					tcl.connectTo(other, entr, false);
				}
				ocl = other.getEntryClearing(entr, true);
				tcl = getEntryClearing(i, true);
				if (ocl != null && tcl != null) {
					ocl.connectTo(this, i, true);
					tcl.connectTo(other, entr, true);
				}
			}
		}
	}

	/**
	 * creates a tile only good enough for drawing. this tile is not connected
	 * to anything and has no mechanism
	 * 
	 * @param serializedTile
	 */
	public HexTile(SerializedTile serializedTile, boolean parent) {
		if (!parent) {
			Map<Integer, SerializedClearing> sClearings = serializedTile
					.getClearings();
			clearings = new HashMap<Integer, Clearing>();
			// get the actual clearings
			for (Integer i : sClearings.keySet()) {
				clearings.put(i, new Clearing(sClearings.get(i)));
			}
		}
		column = serializedTile.getColumn();
		name = serializedTile.getName();
		rotation = serializedTile.getRotation();
		row = serializedTile.getRow();
	}

	public HexTile(TileName parent) {
		this.name = parent;
	}

	public void setRotation(int rot) {
		rotation = rot;
	}

	public SerializedTile getSerializedTile() {
		SerializedTile sTile = new SerializedTile();
		sTile.setPosition(row, column, rotation);
		sTile.setTileName(name);
		// create the serialized clearings.
		Map<Integer, SerializedClearing> sClearings = new HashMap<Integer, SerializedClearing>();
		for (Integer i : clearings.keySet()) {
			sClearings.put(i, clearings.get(i).getSerializedClearing());
		}
		sTile.setClearings(sClearings);
		return sTile;
	}

	@Override
	public Clearing getEntryClearing(int rot) {
		return getEntryClearing(rot, isEnchanted());
	}

	@Override
	public Clearing getEntryClearing(int rot, boolean enchant) {
		int entr = exits[enchant ? 1 : 0][rot % 6];
		return clearings.get(entr);
	}

	@Override
	public Collection<Clearing> getClearings() {
		return clearings.values();
	}

	@Override
	public Clearing getClearing(int num) {
		return clearings.get(num);
	}

	@Override
	public int getBoardRow() {
		return row;
	}

	@Override
	public int getBoardColumn() {
		return column;
	}

	@Override
	public TileName getName() {
		return name;
	}

	@Override
	public int getRotation() {
		return rotation;
	}

	@Override
	public boolean isEnchanted() {
		return enchanted;
	}

	@Override
	public void connectTo(TileName otherTile, int exit) {
		surroundings[exit % 6] = otherTile;

	}

	@Override
	public List<TileName> getSurrounding() {
		return IterationTools.notNull(surroundings);
	}

	protected Board getParent() {
		return parent;
	}

	private void clearExits() {
		for (int i = 0; i < exits.length; ++i) {
			int[] tmp = exits[i];
			for (int j = 0; j < tmp.length; ++j) {
				tmp[j] = 0;
			}
		}
	}

	private void connectClearings() {
		PathType n, h, s;
		n = PathType.NORMAL;
		h = PathType.HIDDEN;
		s = PathType.SECRET;
		switch (name) {
		case AWFUL_VALLEY:
			connectAlways(1, 4);
			connectAlways(2, 5);
			exitAlways(5, 0);
			exitAlways(4, 1);
			exitAlways(4, 2);
			exitAlways(2, 4);
			exitAlways(1, 5);
			break;
		case BAD_VALLEY:
			connectAlways(1, 4);
			connectAlways(2, 5);
			exitAlways(4, 0);
			exitAlways(5, 1);
			exitAlways(1, 2);
			exitAlways(2, 4);
			exitAlways(4, 5);
			break;
		case BORDERLAND:
			connectAlways(1, 6);
			connectAlways(2, 3);
			connectAlways(3, 5);
			connectAlways(3, 6);
			connectAlways(4, 5, s);
			connectAlways(4, 6);
			exitAlways(1, 0);
			exitAlways(2, 1);
			exitAlways(4, 2);
			exitAlways(2, 3);
			exitAlways(1, 4);
			exitAlways(5, 5);
			break;
		case CAVERN:
			connect(1, 2, true, s);
			connect(1, 3, false, n);
			connect(1, 4, false, s);
			connect(1, 4, true, n);
			connect(2, 3, false, n);
			connect(2, 6, true, n);
			connect(3, 5, false, s);
			connect(3, 5, true, n);
			connectAlways(3, 6);
			connect(4, 5, false, n);
			connect(4, 5, true, s);
			connectAlways(4, 6);
			exitAlways(2, 0);
			exitAlways(1, 1);
			exitAlways(5, 5);
			break;
		case CAVES:
			connect(1, 4, true, n);
			connect(1, 6, false, n);
			connect(1, 6, true, s);
			connect(2, 3, false, s);
			connect(2, 3, true, n);
			connect(2, 4, false, n);
			connect(3, 4, true, n);
			connect(3, 5, false, n);
			connect(4, 6, false, n);
			connect(5, 6, true, n);
			exitAlways(1, 2);
			exitAlways(2, 4);
			exitAlways(5, 5);
			break;
		case CLIFF:
			connectAlways(1, 6);
			connectAlways(2, 3);
			connectAlways(2, 5, h);
			connectAlways(3, 5);
			connectAlways(3, 6, s);
			connectAlways(4, 6);
			exitAlways(4, 1);
			exitAlways(5, 2);
			exitAlways(2, 4);
			exitAlways(1, 5);
			break;
		case CRAG:
			connectAlways(1, 4);
			connect(1, 6, false, s);
			connectAlways(2, 3, h);
			connectAlways(2, 5);
			connectAlways(3, 5);
			connectAlways(3, 6);
			connect(4, 5, true, s);
			connectAlways(4, 6);
			exitAlways(2, 0);
			break;
		case CURST_VALLEY:
			connectAlways(1, 4);
			connectAlways(2, 5);
			exitAlways(1, 0);
			exitAlways(5, 1);
			exitAlways(4, 2);
			exitAlways(4, 4);
			exitAlways(2, 5);
			break;
		case DARK_VALLEY:
			connectAlways(1, 4);
			connectAlways(2, 5);
			exitAlways(2, 0);
			exitAlways(1, 1);
			exitAlways(5, 2);
			exitAlways(4, 4);
			exitAlways(4, 5);
			break;
		case DEEP_WOODS:
			connect(1, 3, true, n);
			connect(1, 4, false, h);
			connect(1, 4, true, n);
			connect(1, 6, false, n);
			connect(2, 3, false, n);
			connect(2, 5, true, n);
			connect(2, 6, true, n);
			connect(3, 4, true, h);
			connectAlways(3, 5);
			connectAlways(3, 6, h);
			connect(4, 5, false, n);
			connect(4, 6, false, n);
			connect(4, 6, true, h);
			exit(1, 0, false);
			exit(2, 1, false);
			exit(2, 2, false);
			exit(5, 4, false);
			exit(1, 5, false);
			exit(6, 0, true);
			exit(6, 1, true);
			exit(3, 2, true);
			exit(4, 4, true);
			exit(4, 5, true);
			break;
		case EVIL_VALLEY:
			connectAlways(1, 4);
			connectAlways(2, 5);
			exitAlways(4, 0);
			exitAlways(4, 1);
			exitAlways(5, 2);
			exitAlways(1, 4);
			exitAlways(2, 5);
			break;
		case HIGH_PASS:
			connectAlways(1, 4);
			connectAlways(1, 5);
			connectAlways(2, 4);
			connectAlways(3, 6);
			exitAlways(2, 0);
			exitAlways(3, 1);
			exitAlways(5, 3);
			exitAlways(6, 5);
			break;
		case LEDGES:
			connectAlways(1, 3, h);
			connectAlways(1, 4);
			connect(1, 6, false, n);
			connectAlways(2, 5);
			connectAlways(3, 6);
			connectAlways(4, 6, h);
			exitAlways(4, 0);
			exitAlways(2, 1);
			exitAlways(3, 2);
			exitAlways(5, 5);
			break;
		case LINDEN_WOODS:
			connect(2, 4, false, n);
			connect(4, 5, true, n);
			exitAlways(5, 0);
			exitAlways(5, 1);
			exitAlways(2, 2);
			exitAlways(2, 4);
			exitAlways(4, 5);
			break;
		case MAPLE_WOODS:
			connect(2, 4, false, n);
			connect(4, 5, true, n);
			exitAlways(2, 0);
			exitAlways(2, 1);
			exitAlways(4, 2);
			exitAlways(5, 4);
			exitAlways(5, 5);
			break;
		case MOUNTAIN:
			connectAlways(1, 3);
			connect(1, 4, true, s);
			connectAlways(2, 4);
			connectAlways(2, 5);
			connect(3, 6, false, n);
			connect(3, 6, true, h);
			connect(4, 6, false, h);
			connect(4, 6, true, n);
			connect(5, 6, false, n);
			connect(5, 6, true, h);
			exitAlways(4, 0);
			exitAlways(5, 2);
			exitAlways(2, 4);
			break;
		case NUT_WOODS:
			connect(2, 4, false, n);
			connect(4, 5, true, n);
			exitAlways(2, 0);
			exitAlways(4, 1);
			exitAlways(5, 2);
			exitAlways(5, 4);
			exitAlways(2, 5);
			break;
		case OAK_WOODS:
			connect(2, 4, false, n);
			connect(4, 5, true, n);
			exitAlways(5, 0);
			exitAlways(2, 1);
			exitAlways(2, 2);
			exitAlways(4, 4);
			exitAlways(5, 5);
			break;
		case PINE_WOODS:
			connect(2, 4, false, n);
			connect(4, 5, true, n);
			exitAlways(4, 0);
			exitAlways(5, 1);
			exitAlways(5, 2);
			exitAlways(2, 4);
			exitAlways(2, 5);
			break;
		case RUINS:
			connect(1, 2, false, n);
			connect(1, 2, true, h);
			connect(1, 4, false, n);
			connect(1, 4, true, h);
			connect(1, 5, false, h);
			connect(1, 5, true, n);
			connect(2, 6, true, h);
			connectAlways(3, 5);
			connect(3, 6, false, n);
			connect(3, 6, true, s);
			connectAlways(4, 6);
			exitAlways(2, 0);
			exit(2, 1, false);
			exit(6, 1, true);
			exitAlways(3, 2);
			exitAlways(5, 4);
			exitAlways(1, 5);
			break;
		default:
			break;
		}
	}

	private void connectAlways(int a, int b) {
		connectAlways(a, b, PathType.NORMAL);
	}

	private void connectAlways(int a, int b, PathType pt) {
		connect(a, b, false, pt);
		connect(a, b, true, pt);
	}

	private void connect(int a, int b, boolean enchanted, PathType pt) {
		Clearing ca, cb;
		ca = clearings.get(a);
		cb = clearings.get(b);
		connectClearings(ca, cb, enchanted, pt);
	}

	private static void connectClearings(Clearing ca, Clearing cb,
			boolean enchanted, PathType pt) {
		ca.connectTo(cb, enchanted, pt);
		cb.connectTo(ca, enchanted, pt);
	}

	private void exitAlways(int clear, int rot) {
		exit(clear, rot, false);
		exit(clear, rot, true);
	}

	private void exit(int clear, int rot, boolean ench) {
		rot += getRotation();
		int[] exit = exits[ench ? 1 : 0];
		rot %= exit.length;
		exit[rot] = clear;
	}

	private Matrix transform(Point point, Matrix buffer) {
		buffer.set(0, 0, point.getX());
		buffer.set(1, 0, point.getY());
		buffer.set(2, 0, 1f);
		// scale the point to [0,2]X[0,2]
		bufferC.scale(2f, 2f, 1f);
		bufferC.multiply(buffer, buffer);
		// translate the point to [-1,1]X[-1,1]
		bufferC.translate(-1f, -1f);
		bufferC.multiply(buffer, buffer);
		// scale and invert the point
		bufferC.scale(1f, -0.866025f, 1f);
		bufferC.multiply(buffer, buffer);
		// rotate the point
		bufferC.rotate(-Mathf.PI * getRotation() / 3f);
		bufferC.multiply(buffer, buffer);
		// reset the point to 3d instead of 2d homogenous
		buffer.set(2, 0, 0f);
		return buffer;
	}

	private Matrix bufferA;
	private Matrix bufferB;
	private Matrix bufferC;
	private Board parent;
	private int[][] exits;
	private TileName name;
	private int row;
	private int column;
	private Map<Integer, Clearing> clearings;
	private int rotation;
	private TileName[] surroundings;

	public boolean setEnchanted(boolean b) {
		this.enchanted = b;		
		return this.enchanted;
	}
}
