package model.counter.chit;

import model.enums.SoundType;
import model.enums.TileName;

public class SoundChit extends Chit {

	public SoundType getType() {
		return sound;
	}

	// returns the source of the disturbance
	public int getSource() {
		return clearing;
	}

	protected SoundChit(TileName tt, SoundType snd, int clear) {
		super(tt);
		sound = snd;
		clearing = clear;
	}

	private SoundType sound;
	private int clearing;
}
