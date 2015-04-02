package communication.handler.server;

import java.util.HashMap;
import java.util.Map;

import model.character.Character;
import client.ClientController;
import communication.ClientNetworkHandler;
import communication.handler.server.serialized.SerializedBoard;

public class StartGame implements ClientNetworkHandler {
	private static final long serialVersionUID = 1093417167119495512L;
	private SerializedBoard board;
	private Map<Integer, Character> characters;
	public StartGame(SerializedBoard sboard,
			HashMap<Integer, Character> characters) {
		this.characters = characters;
		this.board = sboard;
	}

	@Override
	public void handle(ClientController controller) {
		controller.setAllCharacters(characters);
		controller.startGame(board);		
	}

}
