package communication.handler.server;

import client.ClientController;
import communication.ClientNetworkHandler;

public class RequestSearchInformation implements ClientNetworkHandler {

	@Override
	public void handle(ClientController controller) {
		controller.requestSearchInformation();

	}
	@Override
	public String toString(){
		return "request search info handler.";		
	}
}
