package model.board;

// a chit is placed on the map. it is one of the following: 
// sound chits,
// warning chits,
// site chits,
// action chits.

public class Chit {
	protected ChitType type;
	protected boolean flipped;	//unflipped chits, mean they are face down.

	public ChitType getType() {
		return type;
	}

	public void setType(ChitType type) {
		this.type = type;
	}

	public boolean isFlipped() {
		return flipped;
	}

	public void setFlipped(boolean flipped) {
		this.flipped = flipped;
	}
}
