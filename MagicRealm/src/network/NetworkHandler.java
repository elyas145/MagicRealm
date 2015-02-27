package network;

import java.io.Serializable;

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

public interface NetworkHandler<T> extends Handler<T>, Serializable {
}
