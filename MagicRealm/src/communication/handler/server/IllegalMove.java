package communication.handler.server;

import model.enums.TileName;
import client.ClientController;
import communication.ClientNetworkHandler;

public class IllegalMove implements ClientNetworkHandler{
	private static final long serialVersionUID = 7304552872110334573L;
	private TileName tile;
	private int clearing;
	
	public IllegalMove(TileName tile, int clearing){
		this.tile = tile;
		this.clearing = clearing;
	}
	@Override
	public void handle(ClientController controller) {
		controller.displayMessage("Illegal Move. You cannot move to " + tile.toString() + "clearing " + clearing);		
	}
	@Override
	public String toString(){
		return "illegal move handler.";		
	}

}
