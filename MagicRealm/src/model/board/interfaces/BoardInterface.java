/**
 * This interface lists the public Board functions.
 * If you need me to implement a function, Please add it here, with a comment describing what goes in, and what should come out.
 * For clarity and consistancy reasons, we should implement our own classes separatly. 
 * If you see a mistake in my class, ask me to fix it and vice versa. This way we don't waste time discussing possible solutions, 
 * since the implementation doesn't really matter as long as it works well.
 */
package model.board.interfaces;

import java.util.Iterator;

import model.board.HexTile;

public interface BoardInterface {
	
	/**
	 * @return iterator over the board tiles
	 */
	public Iterator<HexTile> iterator();
	
	
	
}
