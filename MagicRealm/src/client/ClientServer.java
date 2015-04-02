package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import communication.ServerNetworkHandler;

public class ClientServer {
	private Socket socket = null;
	private Thread thread = null;
	private ClientThread client = null;
	private ObjectInputStream streamIn = null;
	private ObjectOutputStream streamOut = null;
	private boolean connected = false;
	private ControllerMain controller;

	public ClientServer(ControllerMain controller) {
		this.controller = controller;
	}

	public void connect(String ip, int port) throws UnknownHostException,
			IOException {
		System.out.println("Establishing connection. Please wait ...");
		if (!connected) {
			try {
				this.socket = new Socket(ip, port);
				System.out.println("Connected to server: "
						+ socket.getInetAddress());
				connected = true;
			} catch (UnknownHostException e) {
				System.err.println("" + "unknown host");
				// Trace.exception(e, this);
				throw e;
			} catch (IOException e) {
				System.out.println("Unexpected exception while connecting");
				// Trace.exception(e,this);
				throw e;
			}
		} else
			System.out.println("already connected to server.");

		// setup in and out stream
		try {
			streamOut = new ObjectOutputStream(socket.getOutputStream());
			streamOut.flush();
			streamIn = new ObjectInputStream(socket.getInputStream());
			if (thread == null) {
				client = new ClientThread(this, socket);
				client.setInputStream(streamIn);
				client.start();
			}
		} catch (IOException e) {
			System.out.println("Error opening Data Input Stream");
			throw e;
		}
	}

	public synchronized boolean send(ServerNetworkHandler o) {
		if (streamOut != null) {
			try {
				System.out.println("sending object to server: " + o.toString());
				//streamOut.reset();
				streamOut.writeObject(o);
				//streamOut.flush();
			} catch (IOException e) {
				System.out.println("failed to send object.");
				return false;
			}
			return true;
		} else {
			return false;
		}
	}

	public void handle(Object o) {
		controller.handle(o);
	}
}
