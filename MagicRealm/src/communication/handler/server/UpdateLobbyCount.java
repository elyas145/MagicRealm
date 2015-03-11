package communication.handler.server;

import client.ClientController;
import communication.ClientNetworkHandler;

public class UpdateLobbyCount implements ClientNetworkHandler{
	private int count;
	public UpdateLobbyCount(int count) {
		this.count = count;
	}
	@Override
	public void handle(ClientController controller) {
		controller.updateLobbyCount(count);
		
	}

}
