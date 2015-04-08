package communication.handler.client;

import server.ServerController;
import communication.ServerNetworkHandler;
import model.enums.CharacterType;
import model.enums.CounterType;

public class CharacterSelected implements ServerNetworkHandler{
	private static final long serialVersionUID = -1347594998974805965L;
	private CharacterType character;
	private int ID;
	private CounterType location;
	public CharacterSelected(int ID, CharacterType character, CounterType loc){
		this.ID = ID;
		this.character = character;
		this.location = loc;
	}

	@Override
	public void handle(ServerController controller) {
		controller.setCharacter(ID, character, location);
		
	}
	
	@Override
	public String toString(){
		return "Character selected handler.";
	}
}
