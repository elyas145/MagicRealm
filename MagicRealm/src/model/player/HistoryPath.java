package model.player;
import model.enums.PathType;
import model.enums.TileName;
public class HistoryPath {
	private TileName tile;
	private boolean enchanted;
	private PathType type;
	public PathType getType() {
		return type;
	}

	public void setType(PathType type) {
		this.type = type;
	}

	public TileName getTile() {
		return tile;
	}

	public void setTile(TileName tile) {
		this.tile = tile;
	}

	public boolean isEnchanted() {
		return enchanted;
	}

	public void setEnchanted(boolean enchanted) {
		this.enchanted = enchanted;
	}

	public int getClearingOne() {
		return cl1;
	}

	public void setClearingOne(int cl1) {
		this.cl1 = cl1;
	}

	public int getClearingTwo() {
		return cl2;
	}

	public void setClearingTwo(int cl2) {
		this.cl2 = cl2;
	}

	private int cl1;
	private int cl2;
	
	public HistoryPath(TileName tile, int cl1, int cl2, boolean ench, PathType type){
		this.tile = tile;
		this.cl1 = cl1;
		this.cl2 = cl2;
		enchanted = ench;
		this.type = type;
	}
}
