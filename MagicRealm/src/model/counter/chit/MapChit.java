package model.counter.chit;

import model.enums.ChitType;
import model.enums.MapChitType;
import model.enums.TileName;

public class MapChit extends Chit{
	
	private MapChitType type;
	private char identifier;
	
	public char getIdentifier() {
		return identifier;
	}

	public void setIdentifier(char identifier) {
		this.identifier = identifier;
	}

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
	
	public MapChit(MapChitType type) {
		setFlipped(false);
		this.type = type;
	}
	
	public MapChit(TileName tt, int clearing) {
		setFlipped(false);
		setTile(tt);
		setClearing(clearing);
	}
	
	public MapChit(MapChitType type, char identifier) {
		this.type = type;
		this.identifier = identifier;
	}
}
