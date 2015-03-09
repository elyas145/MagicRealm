package communication.handler.server;

import java.io.Serializable;
import java.util.Map;

import model.board.clearing.Clearing;
import model.counter.chit.Chit;
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
	private Map<Integer, Clearing> clearings;
	private Map<Chit, Clearing> chitMap;

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

	public Map<Integer, Clearing> getClearings() {
		return clearings;
	}

	public Map<Chit, Clearing> getChitMap() {
		return chitMap;
	}

	public void setPosition(int row, int column, int rotation) {
		this.row = row;
		this.column = column;
		this.rotation = rotation;
	}

	public void setTileName(TileName name) {
		this.name = name;
	}

	public void setClearings(Map<Integer, Clearing> clearings) {
		this.clearings = clearings;

	}

	public void setChitMap(Map<Chit, Clearing> chitMap) {
		this.chitMap = chitMap;

	}

}
