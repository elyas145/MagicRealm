package model.board;

public class HexTile {
	
	/*
	 * Board should be set up so every odd row has one less hex / hex is shifted
	 * 		_   _   _   _   _
	 * 	0  /0\_/1\_/2\_/3\_/4\
	 * 	1  \_/0\_/1\_/2\_/3\_/
	 * 	2  /0\_/1\_/2\_/3\_/4\_
	 * 	3  \_/0\_/1\_/2\_/3\_/4\
	 * 	4	 \_/ \_/ \_/ \_/ \_/
	 */
	
	public HexTile(TileType tp, int rw, int col) {
		type = tp;
		row = rw;
		column = col;
	}
	
	public int getBoardRow() {
		return row;
	}
	
	public int getBoardColumn() {
		return column;
	}
	
	public TileType getType() {
		return type;
	}
	
	private TileType type;
	private int row;
	private int column;
	
}
