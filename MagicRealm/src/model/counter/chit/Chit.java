package model.counter.chit;

import java.io.Serializable;

import model.enums.TileName;
import model.interfaces.ChitInterface;

// a chit is placed on the map. it is one of the following: 
// sound chits,
// warning chits,
// site chits,
// action chits.
// treasure chits

public abstract class Chit extends ChitInterface implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7004673221116854940L;
	public boolean isFlipped() {
		return flipped;
	}

	public void setFlipped(boolean flipped) {
		this.flipped = flipped;
	}

	@Override
	public TileName getTile() {
		return tile;
	}

	@Override
	public int getClearing() {
		return clearing;
	}
	
	protected void setClearing(int c) {
		clearing = c;
	}
	
	protected Chit() {
		flipped = false;
		tile = null;
		clearing = 0;
	}
	
	protected Chit(TileName tt) {
		flipped = false;
		tile = tt;
		clearing = 0;
	}
	
	protected Chit(TileName tt, int clear) {
		flipped = false;
		tile = tt;
		clearing = clear;
	}
	
	public void setTile(TileName name){
		this.tile = name;
	}
	
	@Override
	public String toString() {
		return "Chit: flip: " + flipped + ", tile: " + tile + ", clearing: " + clearing;
	}
	
	private boolean flipped;	//unflipped chits, mean they are face down.
	private TileName tile;
	private int clearing;
}
