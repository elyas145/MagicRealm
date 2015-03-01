package controller.network.server.handlers;

import controller.ClientController;
import network.NetworkHandler;

public class EnterLobby implements NetworkHandler<ClientController> {

	@Override
	public void handle(ClientController control) {
		control.enterLobby();
	}
	
	private static final long serialVersionUID = -53116425386558057L;

}
