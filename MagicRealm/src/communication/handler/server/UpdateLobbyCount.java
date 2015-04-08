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
	
	@Override
	public String toString(){
		return "update lobby count handler.";		
	}
	
	private static final long serialVersionUID = 1602003275368613137L;

}
