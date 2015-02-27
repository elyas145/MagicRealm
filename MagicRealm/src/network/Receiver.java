package network;

import java.io.IOException;

import utils.handler.Invoker;

public interface Receiver<T> {

	public void receive(Invoker<T> data) throws IOException, ClassNotFoundException, ConnectionClosedException;

}
