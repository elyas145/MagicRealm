package controller.network.server.handlers;

import model.exceptions.MaximumPlayersException;
import network.NetworkHandler;
import controller.ClientController;

public class ServerFull implements NetworkHandler<ClientController> {

	@Override
	public void handle(ClientController client) {
		client.raiseException(new MaximumPlayersException());
	}
	
	private static final long serialVersionUID = -1612596613444247407L;

}
