package client.copy;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ClientThreadCopy extends Thread {
	private Socket socket = null;
	private ClientServerCopy server = null;
	private ObjectInputStream streamIn = null;
	private boolean done = false;

	public ClientThreadCopy(ClientServerCopy server, Socket socket) {
		this.server = server;
		this.socket = socket;
	}

	public void setInputStream(ObjectInputStream stream) {
		streamIn = stream;
	}

	public void close() {
		done = true;
		try {
			if (streamIn != null)
				streamIn.close();
		} catch (IOException e) {
			System.out.println("Error closing input stream");
			// Trace.exception(e,this);
		}
	}

	public void run() {
		System.out.println("Client Thread " + socket.getLocalPort()
				+ " running.");
		while (!done) {
			try {
				server.handle(streamIn.readObject());
			} catch (Exception e) {
				System.out.println("Listening error");
				e.printStackTrace();
				break;
				// Trace.exception(e, this);
			}
		}
	}
}
