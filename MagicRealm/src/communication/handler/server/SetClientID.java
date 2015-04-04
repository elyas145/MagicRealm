package communication.handler.server;

import communication.ClientNetworkHandler;
import client.ClientController;

public class SetClientID implements ClientNetworkHandler {
	private static final long serialVersionUID = 3401751799578398290L;
	private int id;
	public SetClientID(int id){
		this.id = id;
	}
	@Override
	public void handle(ClientController controller) {
		controller.setID(id);		
	}
	@Override
	public String toString(){
		return "set id handler.";		
	}
}
