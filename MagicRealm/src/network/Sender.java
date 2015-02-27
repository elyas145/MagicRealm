package network;

import java.io.IOException;

public interface Sender<T> {

	public void send(NetworkHandler<T> nh) throws IOException;

}
