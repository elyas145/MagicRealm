package lwjglview.graphics.board.tile.clearing;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;

import config.GraphicsConfiguration;
import lwjglview.graphics.board.tile.LWJGLTileDrawable;
import model.EnchantedHolder;
import model.enums.CounterType;

public class LWJGLClearingStorage {
	public LWJGLClearingStorage(LWJGLTileDrawable td, FloatBuffer tc,
			FloatBuffer nl, FloatBuffer el) {
		init(td, tc.get(0) + nl.get(0), tc.get(1) + nl.get(1),
				tc.get(0) + el.get(0), tc.get(1) + el.get(1));
	}

	public LWJGLClearingStorage(LWJGLTileDrawable td, FloatBuffer tc) {
		init(td, tc.get(0), tc.get(1), tc.get(0), tc.get(1));
	}

	public void put(CounterType ct) {
		synchronized (chits) {
			if (!chits.contains(ct)) {
				chits.add(ct);
				if (changeDim()) {
					relocateAllChits();
				} else {
					moveChit(ct);
				}
			}
		}
	}

	public void remove(CounterType ct) {
		synchronized (chits) {
			if (chits.contains(ct)) {
				int idx = chits.indexOf(ct);
				chits.remove(ct);
				if (changeDim()) {
					relocateAllChits();
				} else {
					for (; idx < chits.size(); ++idx) {
						moveChit(chits.get(idx));
					}
				}
			}
		}
	}

	public void getLocation(FloatBuffer loc, boolean ench) {
		float[] pos = posns.get(ench);
		loc.put(0, pos[0]);
		loc.put(1, pos[1]);
	}

	public void getLocation(FloatBuffer loc) {
		getLocation(loc, tile.isEnchanted());
	}

	public void getLocation(CounterType chit, FloatBuffer loc) {
		int idx = chits.indexOf(chit);
		int row = dim - idx / dim - 1;
		int col = idx % dim;
		int gaps = dim - 1;
		float spacing = 2f * GraphicsConfiguration.CHIT_SCALE
				+ GraphicsConfiguration.CHIT_SPACING;
		float offs = spacing * gaps * .5f;
		synchronized(buff) {
			getLocation(buff);
		}
		loc.put(0, col * spacing - offs + buff.get(0));
		loc.put(1, row * spacing - offs + buff.get(1));
	}

	private void init(LWJGLTileDrawable td, float f, float g, float h, float i) {
		tile = td;
		posns = new EnchantedHolder<float[]>(new float[] { f, g }, new float[] {
				h, i });
		chits = new ArrayList<CounterType>();
		dim = 0;
		buff = BufferUtils.createFloatBuffer(2);
	}

	private boolean changeDim() {
		double sqrt = Math.sqrt(chits.size());
		int tmp = (int) sqrt;
		if (sqrt % 1. > .0001) {
			tmp += 1;
		}
		if (tmp != dim) {
			dim = tmp;
			return true;
		}
		return false;
	}

	private void relocateAllChits() {
		synchronized (chits) {
			for (CounterType type : chits) {
				moveChit(type);
			}
		}
	}

	private void moveChit(CounterType ct) {
		synchronized (buff) {
			getLocation(ct, buff);
			tile.relocateChit(ct, buff.get(0), buff.get(1));
		}
	}

	private LWJGLTileDrawable tile;
	private EnchantedHolder<float[]> posns;
	private int dim;
	private List<CounterType> chits;
	private FloatBuffer buff;
}