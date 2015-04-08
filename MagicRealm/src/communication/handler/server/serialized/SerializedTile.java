package communication.handler.server.serialized;

import java.io.Serializable;
import java.util.Map;

import model.enums.TileName;

public class SerializedTile implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5288437026727195884L;
	private int row;
	private int column;
	private int rotation;
	private TileName name;
	private Map<Integer, SerializedClearing> clearings;

	public int getRow() {
		return row;
	}

	public int getColumn() {
		return column;
	}

	public int getRotation() {
		return rotation;
	}

	public TileName getName() {
		return name;
	}

	public Map<Integer, SerializedClearing> getClearings() {
		return clearings;
	}

	public void setPosition(int row, int column, int rotation) {
		this.row = row;
		this.column = column;
		this.rotation = rotation;
	}

	public void setTileName(TileName name) {
		this.name = name;
	}

	public void setClearings(Map<Integer, SerializedClearing> clearings) {
		this.clearings = clearings;

	}


}
