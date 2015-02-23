package lwjglview.graphics.board.tile.clearing;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;

import config.GraphicsConfiguration;
import lwjglview.graphics.board.tile.LWJGLCounterStorage;
import lwjglview.graphics.board.tile.LWJGLTileDrawable;

public class LWJGLClearingStorage extends LWJGLCounterStorage {
	public LWJGLClearingStorage(LWJGLTileDrawable td, FloatBuffer tc,
			FloatBuffer nl, FloatBuffer el) {
		super(td, tc.get(0) + nl.get(0), tc.get(1) + nl.get(1),
				tc.get(0) + el.get(0), tc.get(1) + el.get(1));
		chits = new ArrayList<Integer>();
		dim = 0;
		buff = BufferUtils.createFloatBuffer(2);
	}

	public void getLocation(int id, FloatBuffer loc) {
		int idx = chits.indexOf(id);
		int row = dim - idx / dim - 1;
		int col = idx % dim;
		int gaps = dim - 1;
		float spacing = 2f * GraphicsConfiguration.CHIT_SCALE
				+ GraphicsConfiguration.CHIT_SPACING;
		float offs = spacing * gaps * .5f;
		synchronized (buff) {
			getLocation(buff);
		}
		loc.put(0, col * spacing - offs + buff.get(0));
		loc.put(1, row * spacing - offs + buff.get(1));
	}

	public void put(int id) {
		synchronized (chits) {
			if (!chits.contains(id)) {
				chits.add(id);
				if (changeDim()) {
					relocateAllChits();
				} else {
					moveChit(id);
				}
			}
		}
	}

	public void remove(int id) {
		synchronized (chits) {
			if (chits.contains(id)) {
				int idx = chits.indexOf(id);
				chits.remove((Integer) id);
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
			for (Integer id : chits) {
				moveChit(id);
			}
		}
	}
	
	private int dim;
	private List<Integer> chits;
	private FloatBuffer buff;
}