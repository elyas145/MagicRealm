package communication;

import java.io.Serializable;

import client.ClientController;

/**
 * 
 * @author taylor
 *
 * @param <T>
 * 
 *            A serializable version of a handler, so it may be sent over a
 *            network.
 * 
 */

public interface ClientNetworkHandler extends Serializable {

	void handle(ClientController controller);
	String toString();
}
