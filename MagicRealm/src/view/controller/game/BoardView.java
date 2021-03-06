package view.controller.game;

import model.counter.chit.MapChit;
import model.enums.CounterType;
import model.enums.TileName;
import model.enums.TimeOfDay;
import model.interfaces.ClearingInterface;

public interface BoardView {

	void setTile(TileName tile, int row, int col, int rot,
			Iterable<? extends ClearingInterface> clearings);

	void setTileEnchanted(TileName tile, boolean ench);

	void setCounter(CounterType counter, TileName tile, int clearing);
	
	void setCounter(CounterType counter, TileName tile);
	
	void focusOn(TileName tile);
	
	void focusOn(CounterType counter);
	
	void focusOn(TileName tile, int clearing);
	
	boolean isAnimationFinished(CounterType ct);
	
	void setTimeOfDay(TimeOfDay time);

	void hideCounter(CounterType currentCounter);
	
	void unHideCounter(CounterType currentCounter);
	
	void setMapChit(MapChit chit);
	
	void hideAllMapChits();
	
	void revealMapChit(MapChit chit);
	
	void replaceMapChit(MapChit chit, Iterable<MapChit> replacements);
	
	void removeMapChit(MapChit chit);

	void revealAllMapChits(Iterable<MapChit> discoveredMapChits);

	void loadMapChits(Iterable<MapChit> chits);

}
