package communication.handler.client;

import server.ServerController;
import communication.ServerNetworkHandler;
import model.enums.CharacterType;
import model.enums.ValleyChit;

public class CharacterSelected implements ServerNetworkHandler{
	private static final long serialVersionUID = -1347594998974805965L;
	private CharacterType character;
	private int ID;
	private ValleyChit location;
	public CharacterSelected(int ID, CharacterType character, ValleyChit location){
		this.ID = ID;
		this.character = character;
		this.location = location;
	}

	@Override
	public void handle(ServerController controller) {
		controller.setCharacter(ID, character, location);
		
	}
}
