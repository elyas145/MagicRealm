package communication;

import java.io.Serializable;

import server.ServerController;

public interface ServerNetworkHandler extends Serializable {

	void handle(ServerController controller);
}

