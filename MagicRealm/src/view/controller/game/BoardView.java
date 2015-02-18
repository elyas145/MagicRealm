package view.controller.game;

import model.enums.CounterType;
import model.enums.TileName;
import model.interfaces.ClearingInterface;

public interface BoardView {

	void setTile(TileName tile, int xPos, int yPos, int rot,
			Iterable<ClearingInterface> clearings);

	void enchantTile(TileName tile);

	void setCounter(CounterType counter, TileName tile, int clearing);

}
