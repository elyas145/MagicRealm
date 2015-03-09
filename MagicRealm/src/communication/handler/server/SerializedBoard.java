package communication.handler.server;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import model.enums.CounterType;
import model.enums.MapChitType;
import model.enums.TileName;
import model.interfaces.ClearingInterface;

public class SerializedBoard implements Serializable{
	private static final long serialVersionUID = 4842931638624923336L;
	private Map<Integer, Map<Integer, TileName>> mapOfTileLocations;

	public Map<Integer, Map<Integer, TileName>> getMapOfTileLocations() {
		return mapOfTileLocations;
	}

	public Map<TileName, SerializedTile> getsMapOfTiles() {
		return sMapOfTiles;
	}

	public Map<TileName, int[]> getTileLocations() {
		return tileLocations;
	}

	public Map<MapChitType, TileName> getMapChitLocations() {
		return mapChitLocations;
	}

	public Map<CounterType, ClearingInterface> getCounterPositions() {
		return counterPositions;
	}

	private Map<TileName, SerializedTile> sMapOfTiles = new HashMap<TileName, SerializedTile>();
	private Map<TileName, int[]> tileLocations;
	private Map<MapChitType, TileName> mapChitLocations;
	private Map<CounterType, ClearingInterface> counterPositions;

	public void setMapOfTileLocations(
			Map<Integer, Map<Integer, TileName>> mapOfTileLocations) {
		this.mapOfTileLocations = mapOfTileLocations;
	}

	public void setMapOfTiles(Map<TileName, SerializedTile> sMapOfTiles) {
		this.sMapOfTiles = sMapOfTiles;
	}

	public void setTileLocations(Map<TileName, int[]> tileLocations) {
		this.tileLocations = tileLocations;

	}

	public void setMapChitLocations(Map<MapChitType, TileName> mapChitLocations) {
		this.mapChitLocations = mapChitLocations;

	}

	public void setCounterPositions(
			Map<CounterType, ClearingInterface> counterPositions) {
		this.counterPositions = counterPositions;

	}

}
