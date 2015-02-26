package lwjglview.graphics.board.tile.clearing;

import java.util.ArrayList;
import java.util.List;

import utils.math.linear.Matrix;

import config.GraphicsConfiguration;
import lwjglview.graphics.board.tile.LWJGLCounterStorage;
import lwjglview.graphics.board.tile.LWJGLTileDrawable;

public class LWJGLClearingStorage extends LWJGLCounterStorage {
	public LWJGLClearingStorage(LWJGLTileDrawable td, Matrix norm, Matrix ench) {
		super(td, norm, ench);
		chits = new ArrayList<Integer>();
		dim = 0;
		vec3 = Matrix.zeroVector(3);
		buffer3 = Matrix.zeroVector(3);
	}

	// get the location of a counter in the clearing
	public void getLocation(int id, Matrix loc) {
		int idx = chits.indexOf(id);
		int row = dim - idx / dim - 1;
		int col = idx % dim;
		int gaps = dim - 1;
		float spacing = 2f * GraphicsConfiguration.CHIT_SCALE
				+ GraphicsConfiguration.CHIT_SPACING;
		float offs = spacing * gaps * .5f;
		synchronized (vec3) {
			getLocation(vec3);
			buffer3.fill(col * spacing - offs, row * spacing - offs, 0f);
			vec3.add(buffer3, loc);
		}
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
	private Matrix vec3;
	private Matrix buffer3;
}