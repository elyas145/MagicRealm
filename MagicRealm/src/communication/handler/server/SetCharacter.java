package communication.handler.server;

import client.ClientController;
import communication.ClientNetworkHandler;

public class SetCharacter implements ClientNetworkHandler{
	private static final long serialVersionUID = -6347980596636163326L;
	private model.character.Character character;
	public SetCharacter(model.character.Character character){
		this.character = character;
	}
	@Override
	public void handle(ClientController controller) {
		controller.setCharacter(character);
		
	}
	@Override
	public String toString(){
		return "set character.";		
	}

}
