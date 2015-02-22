/**
 * This interface lists the public Board functions.
 * If you need me to implement a function, Please add it here, with a comment describing what goes in, and what should come out.
 * For clarity and consistancy reasons, we should implement our own classes separatly. 
 * If you see a mistake in my class, ask me to fix it and vice versa. This way we don't waste time discussing possible solutions, 
 * since the implementation doesn't really matter as long as it works well.
 */
package model.interfaces;

import model.enums.CharacterType;
import model.enums.CounterType;
import model.enums.TileName;
import model.enums.ValleyChit;

public interface BoardInterface {
  
  /**
   * Board should set up the tiles in the following fashion
   *    _   _   _   _
   * 0 /0\_/1\_/2\_/3\_
   * 1 \_/0\_/1\_/2\_/3\
   * 2 /0\_/1\_/2\_/3\_/
   * 3 \_/0\_/1\_/2\_/3\
   *     \_/ \_/ \_/ \_/
   */
	
	/**
	 * @return iterator over the board tiles
	 */
	public Iterable<? extends HexTileInterface> iterateTiles();

	public ClearingInterface getLocationOfCounter(CounterType counter);

	public ClearingInterface getClearing(TileName tt, int clearing);

	public void moveCharacter(CharacterType characterType, TileName tt,
			int clearing);

	public void removeCharacter(CharacterType type);

	public void setLocationOfCounter(CounterType counter, ValleyChit initialSite);

	public void setLocationOfCounter(CounterType characterAmazon,
			TileName cliff, int i);
	
}
