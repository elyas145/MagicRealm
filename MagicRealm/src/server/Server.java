package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import communication.ServerNetworkHandler;

import model.enums.MapChitType;
import model.enums.TileName;
import client.ClientThread;
import config.GameConfiguration;

public class Server implements Runnable {
	private ServerSocket server = null;
	private Thread thread = null;
	private ServerController controller;
	private int port;
	private boolean ready = false;

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
			controller = new ServerController(this);
			start();
		} catch (IOException e) {
			// Trace.exception(e);
			System.out.println("unable to start server with given port.");
		}
	}
	public void addThread(Socket socket) {
		System.out.println("client request: " + socket);
		if(ready){
			controller.addClient(socket);
		}else{
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
			System.out.println("SERVER: recieved object from client: " + ((ServerNetworkHandler) input).toString());
			((ServerNetworkHandler) input).handle(controller);
		}
	}
	public synchronized void remove(int ID) {
		controller.remove(ID);
	}

	public void addTreasure(MapChitType site, TileName tile,
			Integer value) {
		if(controller == null){
			System.out.println("Server controller not initialized.");
			return;
		}
		controller.addTreasure(site, tile, value);
	}

	public void doneSettingCheatMode() {
		ready  = true;		
	}

	public void addSound(MapChitType sound, TileName tile, Integer clearing) {
		if(controller == null){
			System.out.println("Server controller not initialized.");
			return;
		}
		controller.addSound(sound, tile, clearing);		
	}

	public void addWarning(MapChitType type, TileName tile) {
		if(controller == null){
			System.out.println("Server controller not initialized.");
			return;
		}
		controller.addWarning(type, tile);	
	}
}
