package model.board.chit;

import model.enums.TileType;
import model.interfaces.ChitInterface;

// a chit is placed on the map. it is one of the following: 
// sound chits,
// warning chits,
// site chits,
// action chits.
// treasure chits

public abstract class Chit extends ChitInterface {

	public boolean isFlipped() {
		return flipped;
	}

	public void setFlipped(boolean flipped) {
		this.flipped = flipped;
	}

	@Override
	public TileType getTile() {
		return tile;
	}

	@Override
	public int getClearing() {
		return clearing;
	}
	
	protected Chit() {
		flipped = false;
		tile = null;
		clearing = 0;
	}
	
	protected Chit(TileType tt) {
		flipped = false;
		tile = tt;
		clearing = 0;
	}
	
	protected Chit(TileType tt, int clear) {
		flipped = false;
		tile = tt;
		clearing = clear;
	}
	
	private boolean flipped;	//unflipped chits, mean they are face down.
	private TileType tile;
	private int clearing;
}
