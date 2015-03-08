package communication.handler.server;

import communication.NetworkHandler;

import client.ClientController;

public class ServerClosed implements NetworkHandler {

	private static final long serialVersionUID = 9050621094529563261L;

	@Override
	public void handle(ClientController control) {
		control.exit();
	}

}
