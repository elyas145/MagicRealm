package communication.handler.server.serialized;

import java.io.Serializable;

import model.enums.MapChitType;
import model.enums.TileName;

public class SerializedMapChit implements Serializable{
	private static final long serialVersionUID = -5069129366831298227L;
	private MapChitType type;
	private char identifier;
	private TileName tile;
	public void setTile(TileName tile) {
		this.tile = tile;
	}

	public MapChitType getType() {
		return type;
	}

	public char getIdentifier() {
		return identifier;
	}

	public void setType(MapChitType type) {
		this.type = type;

	}

	public void setIdentifier(char identifier) {
		this.identifier = identifier;

	}

	public TileName getTile() {
		return tile;
	}
public String toString(){
	return "SMapChit: " + type + " identifier: " + identifier + " tile" + tile; 
}
}
