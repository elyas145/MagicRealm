package view.graphics.board;

import model.enums.ChitType;
import model.enums.TileName;

public interface BoardUpdater {
	
	void updateBoard(ChitType chit, TileName tile, int clearing);

}
