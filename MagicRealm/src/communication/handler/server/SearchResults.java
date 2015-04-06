package communication.handler.server;

import java.util.ArrayList;
import java.util.Map;

import model.counter.chit.MapChit;
import model.enums.MapChitType;
import model.enums.SearchType;
import model.interfaces.ClearingInterface;
import client.ClientController;
import communication.ClientNetworkHandler;

public class SearchResults implements ClientNetworkHandler {
	private static final long serialVersionUID = -3006294261338995592L;
	private ArrayList<MapChit> peek;
	private ArrayList<String> discoveredPaths;
	private SearchType type;
	private boolean city = false;
	private boolean castle = false;
	public SearchResults(SearchType type, ArrayList<MapChit> peek, ArrayList<String> discoveredPaths) {
		this.peek = peek;
		this.type = type;
		this.discoveredPaths = discoveredPaths;
	}

	public SearchResults(SearchType type) {
		this.type = type;
	}


	public SearchResults(SearchType type, ArrayList<MapChit> peek,
			Object object, boolean castle, boolean city) {
		this.type = type;
		this.setCastle(castle);
		this.setCity(city);
		this.peek = peek;
	}

	@Override
	public void handle(ClientController controller) {
		switch(type){
		case CLUES:
			controller.peekMapChits(peek);
			break;
		case PATHS:
			controller.DiscoverPaths(discoveredPaths);
			break;
		case CLUES_PATHS:
			if(castle || city) controller.clueLost(city ? MapChitType.LOST_CITY : MapChitType.LOST_CASTLE);
			controller.DiscoverPaths(discoveredPaths);
			controller.peekMapChits(peek);
			break;
		case PASSAGES_CLUES:
			controller.peekMapChits(peek);
			controller.DiscoverPaths(discoveredPaths);
			break;
		case PASSAGES:
			controller.DiscoverPaths(discoveredPaths);
		case DISCOVER_CHITS:
			controller.discoverChits(peek);
		default:
			return;
		}
		

	}
	@Override
	public String toString(){
		return "search result handler";		
	}
	public ArrayList<MapChit> getPeek() {
		if (type == SearchType.CLUES)
			return peek;
		else
			return null;
	}

	public ArrayList<String> getPaths() {
		if (type == SearchType.PATHS)
			return discoveredPaths;
		else
			return null;
	}

	public boolean isCity() {
		return city;
	}

	public void setCity(boolean city) {
		this.city = city;
	}

	public boolean isCastle() {
		return castle;
	}

	public void setCastle(boolean castle) {
		this.castle = castle;
	}

}
