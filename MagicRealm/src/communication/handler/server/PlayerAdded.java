package communication.handler.server;

import client.ClientController;
import communication.ClientNetworkHandler;
import model.character.Character;

public class PlayerAdded implements ClientNetworkHandler {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8313991787355308019L;
	private Character character;
	private int id;
	public PlayerAdded(int id, Character character){
		this.character = character;
		this.id = id;
	}
	@Override
	public void handle(ClientController controller) {
		controller.addCharacter(id, character);

	}

}
