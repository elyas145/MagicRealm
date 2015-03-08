package communication.handler.server;

import communication.NetworkHandler;

import client.ClientController;

public class SetClientID implements NetworkHandler {
	private static final long serialVersionUID = 3401751799578398290L;
	private int id;
	public SetClientID(int id){
		this.id = id;
	}
	@Override
	public void handle(ClientController controller) {
		controller.setID(id);		
	}

}
