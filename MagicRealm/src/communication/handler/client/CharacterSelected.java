package communication.handler.client;

import server.ServerController;
import communication.ServerNetworkHandler;
import model.enums.CharacterType;

public class CharacterSelected implements ServerNetworkHandler{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1347594998974805965L;
	private CharacterType character;
	private int ID;
	public CharacterSelected(int ID, CharacterType character){
		this.ID = ID;
		this.character = character;
	}

	@Override
	public void handle(ServerController controller) {
		controller.setCharacter(ID, character);
		
	}
}
