package controlview;

import model.interfaces.CharacterInterface;

public interface ControlView {
	
	void startBirdsong();
	
	void startCombat(CharacterInterface character);
	
	void startTrading(CharacterInterface character);
	
}
