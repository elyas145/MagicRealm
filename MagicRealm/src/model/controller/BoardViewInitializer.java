package model.controller;


import java.util.ArrayList;
import java.util.List;

import view.controller.game.BoardView;
import model.counter.chit.MapChit;
import model.interfaces.HexTileInterface;
import network.NetworkHandler;

public class BoardViewInitializer implements NetworkHandler<BoardView>{
	private static final long serialVersionUID = 6016976537914282259L;
	private List<HexTileData> tiles;
	private List<MapChit> mapChits;
	
	public BoardViewInitializer(Iterable<HexTileInterface> tiles, Iterable<MapChit> mapChits){
		this.tiles = new ArrayList<HexTileData> ();
		this.mapChits = new ArrayList<MapChit> ();
		for(HexTileInterface tile : tiles){
			this.tiles.add(new HexTileData(tile));
		}
		for(MapChit chit : mapChits){
			this.mapChits.add(chit);
		}
	}
	@Override
	public void handle(BoardView view) {
		for(HexTileInterface tile : tiles){
			view.setTile(tile.getName(), tile.getBoardRow(), tile.getBoardColumn(), tile.getRotation(), tile.getClearings());
		}
		view.loadMapChits(mapChits);		
		for(MapChit chit : mapChits){
			view.setMapChit(chit);
		}
	}
	
}
