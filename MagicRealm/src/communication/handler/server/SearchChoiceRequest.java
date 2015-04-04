package communication.handler.server;

import client.ClientController;
import communication.ClientNetworkHandler;

public class SearchChoiceRequest implements ClientNetworkHandler{
	private static final long serialVersionUID = 7528140135812755047L;

	@Override
	public void handle(ClientController controller) {
		controller.requestSearchChoice();
		
	}

}
