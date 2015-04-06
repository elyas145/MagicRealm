package communication.handler.client;

import model.enums.MapChitType;
import server.ServerController;
import communication.ServerNetworkHandler;

public class UpdateMapChitsRequest implements ServerNetworkHandler {
	private static final long serialVersionUID = -6608491406592710431L;
	private MapChitType type;
	
	public UpdateMapChitsRequest(MapChitType type){
		this.type = type;
	}
	@Override
	public void handle(ServerController controller) {
		controller.updateMapChits(type);

	}

}
