package view.graphics.board;

import model.enums.ChitType;
import model.enums.TileType;

public interface BoardUpdater {
	
	void updateBoard(ChitType chit, TileType tile, int clearing);

}
