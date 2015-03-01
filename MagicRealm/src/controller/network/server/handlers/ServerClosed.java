package controller.network.server.handlers;

import network.NetworkHandler;
import controller.ClientController;

public class ServerClosed implements NetworkHandler<ClientController> {

	private static final long serialVersionUID = 9050621094529563261L;

	@Override
	public void handle(ClientController control) {
		control.exit();
	}

}
