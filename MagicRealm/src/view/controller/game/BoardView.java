package view.controller.game;

import model.enums.CounterType;
import model.enums.TileName;
import model.interfaces.ClearingInterface;

public interface BoardView {

	void setTile(TileName tile, int row, int col, int rot,
			Iterable<? extends ClearingInterface> clearings);

	void enchantTile(TileName tile);

	void setCounter(CounterType counter, TileName tile, int clearing);
	
	void focusOn(TileName tile);
	
	void focusOn(CounterType counter);
	
	void focusOn(TileName tile, int clearing);
	
	boolean isAnimationFinished(CounterType ct);

}
