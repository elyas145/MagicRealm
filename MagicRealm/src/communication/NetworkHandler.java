package communication;

import java.io.Serializable;

import client.ClientController;
import utils.handler.Handler;

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

public interface NetworkHandler extends Serializable {

	void handle(ClientController controller);
}
