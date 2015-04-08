package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import communication.ServerNetworkHandler;
import model.enums.MapChitType;
import model.enums.TileName;
import config.GameConfiguration;

public class Server implements Runnable {
	private ServerSocket server = null;
	private Thread thread = null;
	private ServerController controller;
	private int port;
	private boolean ready = false;
	private LinkedBlockingQueue<Runnable> runQueue;
	private ThreadPoolExecutor threadPool;

	private class RunnableObject implements Runnable {
		private ServerController controller;
		private ServerNetworkHandler handler;

		public RunnableObject(ServerController c, ServerNetworkHandler h) {
			controller = c;
			handler = h;
		}

		@Override
		public void run() {
			handler.handle(controller);

		}

	}

	public Server(int port) {
		this.port = port;
		ServerView view = new ServerView(this);
		view.start();
	}

	public Server(int port, boolean b) {
		this.port = port;
		this.doneSettingCheatMode();
		this.startServer();
		GameConfiguration.Cheat = b;
	}

	public void startServer() {
		System.out.println("Binding server to port " + port + "...");
		try {
			server = new ServerSocket(port);
			server.setReuseAddress(true);
			controller = new ServerController(this);
			runQueue = new LinkedBlockingQueue<Runnable>();
			threadPool = new ThreadPoolExecutor(20, 50, 1000,
					TimeUnit.MILLISECONDS, runQueue);
			start();
		} catch (IOException e) {
			// Trace.exception(e);
			System.out.println("unable to start server with given port.");
		}
	}

	public void addThread(Socket socket) {
		System.out.println("client request: " + socket);
		if (ready) {
			controller.addClient(socket);
		} else {
			System.out.println("Client Rejected. server not ready.");
		}
	}

	public void start() {
		if (thread == null) {
			thread = new Thread(this);
			thread.start();
			System.out.println("server started: " + server + ": "
					+ thread.getId());
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
		if (input instanceof ServerNetworkHandler) {
			System.out.println("SERVER: recieved object from client: "
					+ input);
			threadPool.execute(new RunnableObject(controller,
					(ServerNetworkHandler) input));
		}
	}

	public synchronized void remove(int ID) {
		controller.remove(ID);
	}

	public void addSite(MapChitType site, TileName tile) {
		if (controller == null) {
			System.out.println("Server controller not initialized.");
			return;
		}
		controller.addSite(site, tile);
	}

	public void doneSettingCheatMode() {
		ready = true;
	}

	public void addSound(MapChitType sound, TileName tile) {
		if (controller == null) {
			System.out.println("Server controller not initialized.");
			return;
		}
		controller.addSound(sound, tile);
	}

	public void addWarning(MapChitType type, TileName tile) {
		if (controller == null) {
			System.out.println("Server controller not initialized.");
			return;
		}
		controller.addWarning(type, tile);
	}

	public void setLost(MapChitType lostCity,
			ArrayList<MapChitType> soundAndSiteArray, TileName tile) {
		controller.setLost(lostCity, soundAndSiteArray, tile);
	}
}
