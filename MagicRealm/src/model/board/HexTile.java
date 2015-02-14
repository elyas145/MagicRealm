package model.board;

import java.awt.List;
import java.io.IOException;

import model.enums.TileType;
import model.interfaces.HexTileInterface;

import org.json.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import utils.resources.ResourceHandler;
public class HexTile implements HexTileInterface {

	public HexTile(TileType tp, int rw, int col, int rot) {
		type = tp;
		row = rw;
		column = col;
		rotation = rot;
	}

	public HexTile(TileType tile, int x, int y, int rot, JSONArray arr) {
		type = tile;
		row = x;
		column = y;
		rotation = rot;
		this.arr = arr;
		setClearings();
	}

	public List getClearings() {
		return clearings;
	}

	public void setClearings(List clearings) {
		this.clearings = clearings;
	}

	private void setClearings() {
		// TODO get clearing location from external file, and create the
		// clearings that go with this tile.
		for(int i = 0; i < arr.size(); i++){
			JSONObject obj = (JSONObject) arr.get(i);
			String name = (String)obj.get("tileName");
			if(name.equals(type.name())){
				Boolean enchanted = (Boolean) obj.get("enchanted");
				if(enchanted == true){
					
				}
			}
		}
		
	}

	public int getBoardRow() {
		return row;
	}

	public int getBoardColumn() {
		return column;
	}

  public void setRotation(int rot) {
    rotation = rot;
  }

  @Override
	public TileType getType() {
		return type;
	}

  @Override
  public int getRotation() {
    return rotation;
  }
  
  @Override
  public boolean isEnchanted() {
    // TODO Auto-generated method stub
    return false;
  }

	private TileType type;
	private int row;
	private int column;
	private List clearings;
	private int rotation;
	private JSONArray arr;
}
