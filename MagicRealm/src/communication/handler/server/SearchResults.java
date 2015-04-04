package communication.handler.server;

import java.util.ArrayList;
import java.util.Map;

import model.counter.chit.MapChit;
import model.enums.SearchType;
import model.interfaces.ClearingInterface;
import client.ClientController;
import communication.ClientNetworkHandler;

public class SearchResults implements ClientNetworkHandler {
	ArrayList<MapChit> peek;
	Map<ClearingInterface, ClearingInterface> discoveredPaths;
	SearchType type;

	public SearchResults(SearchType type, ArrayList<MapChit> peek) {
		this.peek = peek;
		this.type = type;
	}

	public SearchResults(SearchType type) {
		this.type = type;
	}

	public SearchResults(SearchType type,
			Map<ClearingInterface, ClearingInterface> discoveredPaths) {
		this.type = type;
		this.discoveredPaths = discoveredPaths;
	}

	public SearchResults(SearchType type, ArrayList<MapChit> peek,
			Map<ClearingInterface, ClearingInterface> paths) {
		this.type = type;
		this.peek = peek;
		this.discoveredPaths = paths;
	}

	@Override
	public void handle(ClientController controller) {
		switch(type){
		case CLUES:
			controller.displayFinishedSearch(type, peek);
			break;
		case PATHS:
			controller.displayFinishedSearch(type, discoveredPaths);
			break;
		case CLUES_PATHS:
			controller.displayFinishedSearch(type, discoveredPaths, peek);
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

	public Map<ClearingInterface, ClearingInterface> getPaths() {
		if (type == SearchType.PATHS)
			return discoveredPaths;
		else
			return null;
	}

}
