package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientThread extends Thread {
	private int ID = -1;
	private Socket socket = null;
	private Server server = null;
	private ObjectInputStream oStreamIn = null;
	private ObjectOutputStream oStreamOut = null;
	private boolean done = false;
	
	public ClientThread(Server server, Socket socket) {
		super();
		this.server = server;
		this.socket = socket;
		this.ID = socket.getPort();
	}
	
	public Integer getID() {
		return this.ID;
	}
	
	public void send(Object o) {
		try {
			oStreamOut.reset();
			oStreamOut.writeObject(o);
			oStreamOut.flush();
		} catch (IOException e) {
			System.out.println(ID + "Error sending message: ");
			// Trace.exception(e);
			server.remove(ID);
		}
	}
	
	public void run() {
		System.out.println("Server Thread " + ID + " running");
		while (!done) {
			try {
				server.handle(ID, oStreamIn.readObject());
			} catch (Exception e) {
				System.out.println(ID + " Error reading input: ");
				server.remove(ID);
				break;
			}
		}
	}
	
	public void open() throws IOException {
		System.out.println(ID + " Opening buffer streams");
		oStreamOut = new ObjectOutputStream(socket.getOutputStream());
		oStreamIn = new ObjectInputStream(socket.getInputStream());

	}
	
	public void close() throws IOException {
		this.done = true;
		if (socket != null)
			socket.close();
		if (oStreamIn != null)
			oStreamIn.close();
	}
}
