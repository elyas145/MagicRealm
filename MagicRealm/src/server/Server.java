package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import client.ClientThread;
import config.GameConfiguration;

public class Server implements Runnable {
	private ServerSocket server = null;
	private Thread thread = null;
	private ClientThread clients[] = new ClientThread[GameConfiguration.MAX_PLAYERS];
	private int clientCount = 0;
	private boolean gameStarted = false;
	private ServerController controller;
	private int port;

	public Server(int port) {
		this.port = port;
		ServerView view = new ServerView(this);
		view.start();
	}

	public void startServer(){
		System.out.println("Binding server to port " + port + "...");
		try {
			server = new ServerSocket(port);
			server.setReuseAddress(true);
			start();
		} catch (IOException e) {
			// Trace.exception(e);
			System.out.println("unable to start server with given port.");
		}
	}
	public void addThread(Socket socket) {
		System.out.println("client request: " + socket);
		controller.addClient(socket);
	}

	public void start() {
		if (thread == null) {
			thread = new Thread(this);
			thread.start();
			System.out.println("server started: " + server + ": "
					+ thread.getId());
			controller = new ServerController(this);
		}
	}

	@Override
	public void run() {
		while (thread != null) {
			try {
				System.out.println("waiting for a client ...");
				addThread(server.accept());
			} catch (IOException e) {
				System.out.println("Server: error accepting client");
				// Trace.exception(e);
			}
		}
	}

	public void stop() {
		try {
			if (thread != null) {
				thread.join();
				thread = null;
			}
		} catch (InterruptedException e) {
			// Trace.exception(e);
			System.out.println("error stopping the server.");
		}
	}

	public synchronized void handle(int ID, Object input) {
		controller.handle(input);
	}
	public synchronized void remove(int ID) {
		controller.remove(ID);
	}
}
