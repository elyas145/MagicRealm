package communication.handler.server;

import communication.ClientNetworkHandler;

import client.ClientController;

public class EnterLobby implements ClientNetworkHandler {
	private int playersNeeded;
	public EnterLobby(int size) {
		playersNeeded = size;
	}
	@Override
	public void handle(ClientController control) {
		control.enterLobby(playersNeeded);
	}	
	private static final long serialVersionUID = -53116425386558057L;

}
