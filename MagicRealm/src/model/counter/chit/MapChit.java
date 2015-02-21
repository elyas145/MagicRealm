package model.counter.chit;

import model.enums.ChitType;
import model.enums.MapChitType;
import model.enums.TileName;

public class MapChit extends Chit{
	
	private MapChitType type;
	
	public MapChitType getType() {
		return type;
	}

	public void setType(MapChitType type) {
		this.type = type;
	}

	public MapChit(TileName tt, MapChitType type) {
		setFlipped(false);
		setTile(tt);
		this.type = type;
	}
	
	public MapChit(TileName tt, int clearing) {
		setFlipped(false);
		setTile(tt);
		setClearing(clearing);
	}
}
