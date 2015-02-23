package lwjglview.graphics.board.tile;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import model.EnchantedHolder;

public abstract class LWJGLCounterStorage {

	public abstract void put(int id);

	public abstract void remove(int id);

	public abstract void getLocation(int id, FloatBuffer buff);

	public LWJGLCounterStorage(LWJGLTileDrawable td, float f, float g, float h, float i) {
		tile = td;
		posns = new EnchantedHolder<float[]>(new float[] { f, g }, new float[] {
				h, i });
		buff = BufferUtils.createFloatBuffer(2);
	}

	public final void getLocation(FloatBuffer loc, boolean ench) {
		float[] pos = getPosition(ench);
		loc.put(0, pos[0]);
		loc.put(1, pos[1]);
	}

	public final void getLocation(FloatBuffer loc) {
		getLocation(loc, getParent().isEnchanted());
	}
	
	protected LWJGLTileDrawable getParent() {
		return tile;
	}
	
	protected float[] getPosition(boolean ench) {
		return posns.get(ench);
	}

	protected void moveChit(int id) {
		synchronized (buff) {
			getLocation(id, buff);
			tile.relocateChit(id, buff.get(0), buff.get(1));
		}
	}

	private LWJGLTileDrawable tile;
	private EnchantedHolder<float[]> posns;
	private FloatBuffer buff;

}
