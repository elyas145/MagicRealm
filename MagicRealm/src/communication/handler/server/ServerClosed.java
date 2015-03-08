package communication.handler.server;

import communication.ClientNetworkHandler;

import client.ClientController;

public class ServerClosed implements ClientNetworkHandler {

	private static final long serialVersionUID = 9050621094529563261L;

	@Override
	public void handle(ClientController control) {
		control.exit();
	}

}
