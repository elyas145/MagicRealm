package controller.network.server.handlers;

import controller.ClientController;
import network.NetworkHandler;

public class EnterLobby implements NetworkHandler<ClientController> {
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
