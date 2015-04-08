package communication.handler.server;

import java.util.ArrayList;

import model.enums.CharacterType;
import communication.ClientNetworkHandler;
import client.ClientController;

public class EnterCharacterSelection implements ClientNetworkHandler{

	/**
	 * 
	 */
	private static final long serialVersionUID = -451149504635937593L;
	private ArrayList<CharacterType> banned;
	public EnterCharacterSelection(ArrayList<CharacterType> object) {
		banned = object;
	}
	@Override
	public void handle(ClientController controller) {
		controller.enterCharacterSelection(banned);
		
	}
	@Override
	public String toString(){
		return "Enter character selection handler";		
	}
	
}
