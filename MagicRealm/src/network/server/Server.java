package network.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import config.NetworkConfiguration;
import network.Connection;
import network.NetworkHandler;
import network.Sender;
import network.SocketConnection;
import network.ThreadSender;
import network.client.Client;
import utils.handler.Handler;
import utils.handler.Invoker;
import utils.handler.PlainInvoker;
import utils.threading.ThreadInvoker;
import utils.threading.Threadable;

public class Server<S, R> implements Sender<S> {

	// public constructor

	public Server() throws IOException {
		init(null, new ServerSocket(NetworkConfiguration.DEFAULT_PORT));
	}

	public Server(R data) throws IOException {
		init(new PlainInvoker<R>(data), new ServerSocket(NetworkConfiguration.DEFAULT_PORT));
	}

	public Server(Invoker<R> data) throws IOException {
		init(data, new ServerSocket(NetworkConfiguration.DEFAULT_PORT));
	}

	public Server(Invoker<R> data, ServerSocket sock) {
		init(data, sock);
	}

	// public methods
	
	public void start() {
		synchronizer.start();
		accepter.start();
	}

	public void stop() {
		synchronizer.stop();
		accepter.stop();
		List<Integer> clientIDs = new ArrayList<Integer>(clients.keySet());
		for(int i: clientIDs) {
			removeConnection(i);
		}
	}

	public void setConnectionHandler(Handler<Integer> handler) {
		connectionHandler = handler;
	}

	public void setDroppedConnectionHandler(Handler<Integer> handler) {
		droppedConnectionHandler = handler;
	}

	public synchronized void send(NetworkHandler<S> handler, int client) {
		clients.get(client).send(handler);
	}

	public synchronized void close(int client) {
		removeConnection(client);
	}


	// public Connection override

	@Override
	public synchronized void send(NetworkHandler<S> handler) {
		for (Client<S, R> ts : clients.values()) {
			ts.send(handler);
		}
	}

	// protected methods

	protected void stopConnection(int id) {
		clients.get(id).stop();
	}

	protected void removeConnection(int id) {
		if (clients.containsKey(id)) {
			clients.get(id).cancel();
			clients.remove(id);
		}
	}

	protected void setData(R dat) {
		invoker = new PlainInvoker<R>(dat);
	}

	// private methods

	private void init(Invoker<R> dat, ServerSocket sock) {
		serverSocket = sock;
		clients = new HashMap<Integer, Client<S, R>>();
		availID = 0;
		invoker = dat;
		connectionHandler = null;
		droppedConnectionHandler = null;
		synchronizer = new ThreadInvoker<Handler<R>>(new Handler<Handler<R>>() {

			@Override
			public void handle(Handler<R> object) {
				if(invoker == null) {
					throw new RuntimeException("The invoker has not been initialized");
				}
				invoker.invoke(object);
			}
			
		});
		accepter = new Threadable() {

			@Override
			public void mainLoop() {
				try {
					while(isRunning()) {
						accept();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		};
	}

	private void accept() throws IOException {
		addConnection(serverSocket.accept());
	}

	private void addConnection(Socket sock) throws IOException {
		int id = availID++;
		SocketConnection<S, R> conn = new SocketConnection<S, R>(sock);
		Client<S, R> client = new Client<S, R>(new Invoker<R>() {

			@Override
			public void invoke(Handler<R> handle) {
				synchronizer.add(new PlainInvoker<Handler<R>>(handle));
			}
			
		}, conn, new ClientDisconnected(id));
		clients.put(id, client);
		client.start();
		if(connectionHandler != null) {
			connectionHandler.handle(id);
		}
	}
	
	private class ClientDisconnected implements Runnable {
		
		public ClientDisconnected(int id) {
			clientID = id;
		}

		@Override
		public void run() {
			synchronized(droppedConnectionHandler) {
				droppedConnectionHandler.handle(clientID);
			}
		}
		
		private int clientID;
		
	}

	// private object members

	private ServerSocket serverSocket;
	
	private Map<Integer, Client<S, R>> clients;

	private int availID;

	private Invoker<R> invoker;

	private Threadable accepter;

	private Handler<Integer> connectionHandler;

	private Handler<Integer> droppedConnectionHandler;
	
	private ThreadInvoker<Handler<R>> synchronizer;

}
