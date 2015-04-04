package communication.handler.server;

import model.enums.CharacterType;
import client.ClientController;
import communication.ClientNetworkHandler;

public class UpdateCharacterSelection implements ClientNetworkHandler {
	private static final long serialVersionUID = -8014296676484652592L;
	private CharacterType character;

	public UpdateCharacterSelection(CharacterType character) {
		this.character = character;
	}

	@Override
	public void handle(ClientController controller) {
		controller.updateCharacterSelection(character);

	}
	@Override
	public String toString(){
		return "update character selection handler.";		
	}
}
