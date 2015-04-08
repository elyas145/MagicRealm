package lwjglview.controller.board.tile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import config.GraphicsConfiguration;
import lwjglview.controller.board.tile.clearing.LWJGLClearingStorage;
import model.EnchantedHolder;
import utils.math.Mathf;
import utils.math.linear.Matrix;
import utils.random.Random;

public class LWJGLTileStorage extends LWJGLCounterStorage {

	public LWJGLTileStorage(LWJGLTileDrawable td, Matrix pos,
			Iterable<LWJGLClearingStorage> clrs) {
		super(td, pos, pos);
		obstructions = new EnchantedHolder<Collection<Obstruction>>(
				new ArrayList<Obstruction>(), new ArrayList<Obstruction>());
		vec3 = Matrix.zeroVector(3);
		counterRadius = GraphicsConfiguration.CHIT_SCALE * 1.4f;
		outerRadius = GraphicsConfiguration.TILE_WIDTH * .2f;
		clearingRadius = GraphicsConfiguration.CLEARING_RADUS;
		Obstruction self = new Obstruction(pos, outerRadius, true);
		obstructions.get(false).add(self);
		obstructions.get(true).add(self);
		setObstructions(clrs, false);
		setObstructions(clrs, true);
		chits = new HashMap<Integer, Obstruction>();
		getLocation(vec3, false);
		Matrix norm = Matrix.clone(vec3);
		getLocation(vec3, true);
		Matrix ench = Matrix.clone(vec3);
		location = new EnchantedHolder<Matrix>(norm, ench);
	}

	@Override
	public void getLocation(int id, Matrix buff) {
		Obstruction ob;
		synchronized (chits) {
			ob = chits.get(id);
		}
		ob.getLocation(buff);
	}

	/*
	@Override
	public void put(int id) {
		Matrix rand = vec3;
		randomLocation(rand);
		boolean ench = isEnchanted();
		for (int i = 0; i < 20; ++i) {
			for (Obstruction ob : obstructions.get(ench)) {
				if (ob.obstructs(rand)) {
					ob.recommend(rand);
				}
			}
			for (Obstruction ob : chits.values()) {
				if (ob.obstructs(rand)) {
					ob.recommend(rand);
				}
			}
		}
		synchronized (chits) {
			chits.put(id, new Obstruction(rand, counterRadius, false));
		}
		moveChit(id);
	}*/
	
	@Override
	public void put(int id) {
		Matrix rand = vec3;
		randomLocation(rand);
		boolean ench = isEnchanted();
		for (int i = 0; i < 20; ++i) {
			int obst = 0;
			for (Obstruction ob : obstructions.get(ench)) {
				if (ob.obstructs(rand)) {
					++obst;
					ob.recommend(rand);
				}
			}
			for (Obstruction ob : chits.values()) {
				if (ob.obstructs(rand)) {
					++obst;
					ob.recommend(rand);
				}
			}
			if (obst >= 3) {
				randomLocation(rand);
			}
		}
		for (int i = 0; i < 10; ++i) {
			for (Obstruction ob : obstructions.get(ench)) {
				if (ob.obstructs(rand)) {
					ob.recommend(rand);
				}
			}
			for (Obstruction ob : chits.values()) {
				if (ob.obstructs(rand)) {
					ob.recommend(rand);
				}
			}
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
	
	@Override
	protected void getIDs(List<Integer> dest) {
		synchronized(chits) {
			for(int i: chits.keySet()) {
				dest.add(i);
			}
		}
	}

	private void randomLocation(Matrix matr) {
		float r, O;
		r = Mathf.sqrt(Random.random(0f, 1f)) * outerRadius;
		O = Random.random(-Mathf.PI, Mathf.PI);
		matr.set(0, 0, r * Mathf.cos(O));
		matr.set(1, 0, r * Mathf.sin(O));
		matr.add(location.get(isEnchanted()), matr);
	}

	private boolean isEnchanted() {
		return getParent().isEnchanted();
	}

	private void setObstructions(Iterable<LWJGLClearingStorage> clrs, boolean b) {
		Collection<Obstruction> obs = obstructions.get(b);
		for (LWJGLClearingStorage st : clrs) {
			obs.add(new Obstruction(st, b));
		}
	}

	private class Obstruction {
		public Obstruction(Matrix mat, float rad, boolean out) {
			location = Matrix.clone(mat);
			radius = rad;
			isOut = out;
			adjustRadius();
		}

		public Obstruction(LWJGLClearingStorage st, boolean ench) {
			location = Matrix.zeroVector(3);
			st.getLocation(location, ench);
			radius = clearingRadius;
			isOut = false;
			adjustRadius();
		}

		public void getLocation(Matrix buff) {
			buff.copyFrom(location);
		}

		public boolean obstructs(Matrix pos) {
			pos.subtract(location, buffer);
			return buffer.length() < radius ^ isOut;
		}

		public void recommend(Matrix pos) {
			pos.subtract(location, buffer);
			float len = radius / buffer.length();
			len *= isOut ? .95f : 1.05f;
			buffer.multiply(len, buffer);
			location.add(buffer, pos);
		}

		private void adjustRadius() {
			radius += isOut ? -counterRadius : counterRadius;
			buffer = Matrix.zeroVector(3);
		}

		Matrix location;
		Matrix buffer;
		float radius;
		boolean isOut;
	}

	private float outerRadius;
	private float counterRadius;
	private float clearingRadius;
	private EnchantedHolder<Matrix> location;
	private EnchantedHolder<Collection<Obstruction>> obstructions;
	private Map<Integer, Obstruction> chits;
	private Matrix vec3;
}
