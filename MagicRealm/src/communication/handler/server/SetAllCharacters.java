package communication.handler.server;

import java.util.Map;

import client.ClientController;
import communication.ClientNetworkHandler;
import model.character.Character;

public class SetAllCharacters implements ClientNetworkHandler {
	private static final long serialVersionUID = -4964581978832484479L;
	private Map<Integer, Character> characters;
	
	public SetAllCharacters(Map<Integer, Character> map){
		characters = map;
	}
	@Override
	public void handle(ClientController controller) {
		controller.setAllCharacters(characters);
		
	}

}
