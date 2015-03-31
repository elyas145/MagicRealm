package communication.handler.client;

import server.ServerController;
import communication.ServerNetworkHandler;

public class DieRoll implements ServerNetworkHandler {
	private static final long serialVersionUID = -1403583684916112450L;
	private int roll;

	public DieRoll(int roll) {
		this.roll = roll;
	}

	@Override
	public void handle(ServerController controller) {
		controller.setDieRoll(roll);

	}

}
