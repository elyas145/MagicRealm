package lwjglview.graphics.board.tile;

import utils.math.linear.Matrix;

import model.EnchantedHolder;

public abstract class LWJGLCounterStorage {

	public abstract void put(int id);

	public abstract void remove(int id);

	public abstract void getLocation(int id, Matrix buff);

	public LWJGLCounterStorage(LWJGLTileDrawable td, Matrix norm, Matrix ench) {
		tile = td;
		posns = new EnchantedHolder<Matrix>(Matrix.clone(norm), Matrix.clone(ench));
		buff = Matrix.zeroVector(3);
	}

	public final void getLocation(Matrix loc, boolean ench) {
		getPosition(ench, loc);
	}

	public final void getLocation(Matrix loc) {
		getLocation(loc, getParent().isEnchanted());
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

	private LWJGLTileDrawable tile;
	private EnchantedHolder<Matrix> posns;
	private Matrix buff;

}
