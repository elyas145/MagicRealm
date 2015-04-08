package communication.handler.server;

import communication.ClientNetworkHandler;
import communication.handler.server.serialized.SerializedBoard;
import client.ClientController;

public class EnterLobby implements ClientNetworkHandler {
	
	@Override
	public void handle(ClientController control) {
		control.enterLobby();
	}	
	@Override
	public String toString(){
		return "Enter lobby handler.";		
	}
	private static final long serialVersionUID = -53116425386558057L;

}
