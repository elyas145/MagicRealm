package lwjglview.controller.board.tile;

import java.util.ArrayList;
import java.util.List;

import utils.math.linear.Matrix;
import model.EnchantedHolder;

public abstract class LWJGLCounterStorage {

	public abstract void put(int id);

	public abstract void remove(int id);

	public abstract void getLocation(int id, Matrix buff);
	
	protected abstract void getIDs(List<Integer> dest);

	public LWJGLCounterStorage(LWJGLTileDrawable td, Matrix norm, Matrix ench) {
		tile = td;
		posns = new EnchantedHolder<Matrix>(Matrix.clone(norm), Matrix.clone(ench));
		buff = Matrix.zeroVector(3);
		idBuffer = new ArrayList<Integer>();
	}

	public final void getLocation(Matrix loc, boolean ench) {
		getPosition(ench, loc);
	}

	public final void getLocation(Matrix loc) {
		getLocation(loc, getParent().isEnchanted());
	}
	
	public final void resetCounters() {
		synchronized(idBuffer) {
			getIDs(idBuffer);
			for(int i: idBuffer) {
				remove(i);
				put(i);
			}
		}
	}
	
	protected LWJGLTileDrawable getParent() {
		return tile;
	}
	
	protected void getPosition(boolean ench, Matrix pos) {
		pos.copyFrom(posns.get(ench));
	}

	protected void moveChit(int id) {
		synchronized (buff) {
			getLocation(id, buff);
			tile.relocateChit(id, buff);
		}
	}

	private List<Integer> idBuffer;
	private LWJGLTileDrawable tile;
	private EnchantedHolder<Matrix> posns;
	private Matrix buff;

}
