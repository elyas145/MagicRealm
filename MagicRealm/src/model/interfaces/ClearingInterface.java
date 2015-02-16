package model.interfaces;

import java.nio.FloatBuffer;

public interface ClearingInterface {
	
	/**
	 * @return the clearing number
	 */
	int getClearingNumber();
	
	/**
	 * @return into destination
	 * 	       the position of the clearing in the unit square
	 */
	void getTilePosition(boolean enchanted, FloatBuffer dest);

}
