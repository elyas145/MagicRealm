package communication.handler.server;

import communication.ClientNetworkHandler;
import client.ClientController;
import model.exceptions.MaximumPlayersException;

public class ServerFull implements ClientNetworkHandler {

	@Override
	public void handle(ClientController client) {
		client.raiseException(new MaximumPlayersException());
	}
	@Override
	public String toString(){
		return "server full handler.";		
	}
	private static final long serialVersionUID = -1612596613444247407L;

}
