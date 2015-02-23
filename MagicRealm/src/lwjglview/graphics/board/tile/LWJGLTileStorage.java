package lwjglview.graphics.board.tile;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.BufferUtils;

import config.GraphicsConfiguration;
import lwjglview.graphics.board.tile.clearing.LWJGLClearingStorage;
import model.EnchantedHolder;
import utils.math.Mathf;
import utils.math.Matrix;
import utils.random.Random;

public class LWJGLTileStorage extends LWJGLCounterStorage {

	public LWJGLTileStorage(LWJGLTileDrawable td, float f, float g,
			Iterable<LWJGLClearingStorage> clrs) {
		super(td, f, g, f, g);
		obstructions = new EnchantedHolder<Collection<Obstruction>>(
				new ArrayList<Obstruction>(), new ArrayList<Obstruction>());
		buff = BufferUtils.createFloatBuffer(2);
		counterRadius = GraphicsConfiguration.CHIT_SCALE * 1.5f;
		outerRadius = GraphicsConfiguration.TILE_WIDTH * .43f;
		clearingRadius = GraphicsConfiguration.CLEARING_RADUS;
		Obstruction self = new Obstruction(f, g, outerRadius, true);
		obstructions.get(false).add(self);
		obstructions.get(true).add(self);
		setObstructions(clrs, false);
		setObstructions(clrs, true);
		chits = new HashMap<Integer, Obstruction>();
		getLocation(buff, false);
		Matrix norm = Matrix.columnVector(buff.get(0), buff.get(1));
		getLocation(buff, true);
		Matrix ench = Matrix.columnVector(buff.get(0), buff.get(1));
		location = new EnchantedHolder<Matrix>(norm, ench);
	}

	@Override
	public void getLocation(int id, FloatBuffer buff) {
		Obstruction ob;
		synchronized (chits) {
			ob = chits.get(id);
		}
		ob.getLocation(buff);
	}

	@Override
	public void put(int id) {
		Matrix rand = randomLocation();
		boolean ench = isEnchanted();
		for (int i = 0; i < 20; ++i) {
			int obst = 0;
			for (Obstruction ob : obstructions.get(ench)) {
				if (ob.obstructs(rand)) {
					++obst;
					rand = ob.recommend(rand);
				}
			}
			for (Obstruction ob : chits.values()) {
				if (ob.obstructs(rand)) {
					++obst;
					rand = ob.recommend(rand);
				}
			}
			if (obst >= 3) {
				rand = randomLocation();
			}
		}
		for (int i = 0; i < 10; ++i) {
			for (Obstruction ob : obstructions.get(ench)) {
				if (ob.obstructs(rand)) {
					rand = ob.recommend(rand);
				}
			}
			for (Obstruction ob : chits.values()) {
				if (ob.obstructs(rand)) {
					rand = ob.recommend(rand);
				}
			}
		}
		int obstructed = 0;
		for (Obstruction ob : obstructions.get(ench)) {
			if (ob.obstructs(rand)) {
				++obstructed;
			}
		}
		if (obstructed > 0) {
			System.out.println("Counter is obstructed: " + obstructed);
		}
		synchronized (chits) {
			chits.put(id, new Obstruction(rand, counterRadius, false));
		}
		moveChit(id);
	}

	@Override
	public void remove(int id) {
		synchronized (chits) {
			chits.remove(id);
		}
	}

	private Matrix randomLocation() {
		float x = Random.random(-1f, 1f);
		float y = Random.random(-1f, 1f);
		return Matrix.columnVector(x, y).add(location.get(isEnchanted()));
	}

	private boolean isEnchanted() {
		return getParent().isEnchanted();
	}

	private void setObstructions(Iterable<LWJGLClearingStorage> clrs, boolean b) {
		Collection<Obstruction> obs = obstructions.get(b);
		for (LWJGLClearingStorage st : clrs) {
			obs.add(new Obstruction(st));
		}
	}

	private class Obstruction {
		public Obstruction(float f, float g, float rad, boolean out) {
			location = Matrix.columnVector(f, g);
			radius = rad;
			isOut = out;
			adjustRadius();
		}

		public Obstruction(Matrix mat, float rad, boolean out) {
			location = mat;
			radius = rad;
			isOut = out;
			adjustRadius();
		}

		public Obstruction(LWJGLClearingStorage st) {
			st.getLocation(buff);
			location = Matrix.columnVector(buff.get(0), buff.get(1));
			radius = clearingRadius;
			isOut = false;
			adjustRadius();
		}

		public void getLocation(FloatBuffer buff) {
			location.toFloatBuffer(buff);
		}

		public boolean obstructs(Matrix pos) {
			return pos.subtract(location).length() < radius ^ isOut;
		}

		public Matrix recommend(Matrix pos) {
			Matrix delt = pos.subtract(location);
			float len = delt.length();
			float fact = radius;
			fact *= isOut ? .95f : 1.05f;
			return location.add(delt.multiply(fact / len));
		}

		private void adjustRadius() {
			radius += isOut ? -counterRadius : counterRadius;
		}

		Matrix location;
		float radius;
		boolean isOut;
	}

	private float outerRadius;
	private float counterRadius;
	private float clearingRadius;
	private EnchantedHolder<Matrix> location;
	private EnchantedHolder<Collection<Obstruction>> obstructions;
	private Map<Integer, Obstruction> chits;
	private FloatBuffer buff;
}
