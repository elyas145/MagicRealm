package communication.handler.server;

import java.util.ArrayList;

import model.enums.MapChitType;
import client.ClientController;
import communication.ClientNetworkHandler;
import communication.handler.server.serialized.SerializedMapChit;

public class UpdateMapChits implements ClientNetworkHandler {
	private static final long serialVersionUID = 880932431485293753L;
	private ArrayList<SerializedMapChit> mapChits;
	private MapChitType type;
	public UpdateMapChits(MapChitType type, ArrayList<SerializedMapChit> smapchits) {
		mapChits = smapchits;
		this.type = type;
	}

	@Override
	public void handle(ClientController controller) {
		controller.updateMapChits(type, mapChits);

	}

}
