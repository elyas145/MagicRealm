package communication.handler.server;

import model.enums.CharacterType;
import model.enums.TileName;
import client.ClientController;
import communication.ClientNetworkHandler;

public class UpdateLocationOfCharacter implements ClientNetworkHandler {
	private static final long serialVersionUID = -1557059148531443514L;
	private CharacterType character;
	private TileName tile;
	private int clearing;

	public UpdateLocationOfCharacter(CharacterType actor, TileName tile,
			int clearing) {
		this.character = actor;
		this.tile = tile;
		this.clearing = clearing;
	}

	@Override
	public void handle(ClientController controller) {
		controller.moveCounter(character.toCounter(), tile, clearing);
	}
	@Override
	public String toString(){
		return "update location of character handler.";		
	}
}
