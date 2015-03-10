package communication.handler.server;

import java.io.Serializable;
import java.util.List;

import utils.math.linear.Matrix;
import model.EnchantedHolder;
import model.counter.chit.Chit;
import model.enums.TileName;

public class SerializedClearing implements Serializable {
	private static final long serialVersionUID = -1947263645476199141L;
	private List<Chit> chits;
	private int number;
	private EnchantedHolder<Matrix> locations;
	private SerializedTile parent;

	public SerializedTile getParent() {
		return parent;
	}

	public List<Chit> getChits() {
		return chits;
	}

	public void setChits(List<Chit> chits) {
		this.chits = chits;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public EnchantedHolder<Matrix> getLocations() {
		return locations;
	}

	public void setLocations(EnchantedHolder<Matrix> locations) {
		this.locations = locations;
	}

	/**
	 * use this method to avoid infinite recursion when serializing clearings.
	 * since clearings have a parent, and a parent has a set of clearings, the
	 * program will go into infinite recursion trying to serialize the clearings
	 * and their parents.
	 * 
	 * @param boardColumn
	 * @param boardRow
	 * @param name
	 * @param rotation
	 */
	public void setParent(int boardColumn, int boardRow, TileName name,
			int rotation) {
		parent = new SerializedTile();
		parent.setPosition(boardRow, boardColumn, rotation);
		parent.setTileName(name);
	}

}
