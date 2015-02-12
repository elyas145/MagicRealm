package model.board;

// a chit is placed on the map. it is one of the following: 
// sound chits,
// warning chits,
// site chits,
// action chits.
// treasure chits

public class Chit {
	protected boolean flipped;	//unflipped chits, mean they are face down.

	public boolean isFlipped() {
		return flipped;
	}

	public void setFlipped(boolean flipped) {
		this.flipped = flipped;
	}
}
