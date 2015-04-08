package communication.handler.server;

import model.enums.CharacterType;
import client.ClientController;
import communication.ClientNetworkHandler;

public class IllegalCharacterSelection implements ClientNetworkHandler {
	
	private CharacterType type;
	
	public IllegalCharacterSelection(CharacterType type){
		this.type = type;
	}
	
	@Override
	public void handle(ClientController controller) {
		controller.illegalCharacterSelection(type);
	}
	
	private static final long serialVersionUID = -3155520691191352770L;

}
