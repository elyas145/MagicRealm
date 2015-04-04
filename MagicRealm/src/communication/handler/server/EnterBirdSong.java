package communication.handler.server;

import communication.ClientNetworkHandler;
import client.ClientController;

public class EnterBirdSong implements ClientNetworkHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1249916565617049615L;

	@Override
	public void handle(ClientController controller) {
		controller.enterBirdSong();

	}
	@Override
	public String toString(){
		return "Enter bird song handler.";		
	}

}
