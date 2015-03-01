package network.client;

import config.NetworkConfiguration;

import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import network.Connection;
import network.ConnectionClosedException;
import network.NetworkHandler;
import network.Sender;
import network.SocketConnection;
import network.ThreadSender;
import utils.handler.ExceptionHandler;
import utils.handler.Handler;
import utils.handler.Invoker;
import utils.handler.PlainInvoker;
import utils.threading.ThreadInvoker;
import utils.threading.Threadable;

public class Client<S, R> implements Sender<S> {

	// public constructors

	public Client(String addr) throws UnknownHostException, IOException {
		Socket sock = new Socket(addr, NetworkConfiguration.DEFAULT_PORT);
		init(null, new SocketConnection<S, R>(sock));
	}

	public Client(R data, String addr) throws UnknownHostException, IOException {
		Socket sock = new Socket(addr, NetworkConfiguration.DEFAULT_PORT);
		init(new PlainInvoker<R>(data), new SocketConnection<S, R>(sock));
	}

	public Client(R data, Socket sock) throws IOException {
		init(new PlainInvoker<R>(data), new SocketConnection<S, R>(sock));
	}

	public Client(R data, Connection<S, R> conn) {
		init(new PlainInvoker<R>(data), conn);
	}

	public Client(Invoker<R> invk, Connection<S, R> conn) {
		init(invk, conn);
	}

	public Client(Invoker<R> invk, Connection<S, R> conn, Runnable discon) {
		init(invk, conn);
		setDisconnectListener(discon);
	}

	// public methods

	public void setData(R dat) {
		invoker = new PlainInvoker<R>(dat);
	}

	public void setInvoker(Invoker<R> inv) {
		invoker = inv;
	}

	public void setDisconnectListener(Runnable discon) {
		onDisconnect = discon;
	}

	public void start() {
		sender.start();
		receiver.start();
		receiveThread.start();
	}

	public void stop() {
		// close connection
		connection.close();
		// stop receiving new handlers
		receiveThread.stop();
		// stop sending objects
		sender.stop();
		// stop invoking new objects
		receiver.stop();
		if (onDisconnect != null) {
			// disconnect listener
			onDisconnect.run();
			onDisconnect = null;
		}
	}
	
	public void join() {
		receiveThread.join();
		sender.join();
		receiver.join();
	}

	public void cancel() {
		// close connection
		connection.close();
		// stop receiving new handlers
		receiveThread.stop();
		// stop sending handlers
		sender.cancel();
		// cancel receiving handlers
		receiver.cancel();
		if (onDisconnect != null) {
			// disconnected
			onDisconnect.run();
			onDisconnect = null;
		}
	}

	@Override
	public void send(NetworkHandler<S> nh) {
		sender.send(nh);
	}

	// private methods

	private void init(Invoker<R> dat, Connection<S, R> conn) {
		onDisconnect = null;
		invoker = dat;
		connection = conn;
		receiver = new ThreadInvoker<Handler<R>>(new Handler<Handler<R>>() {

			@Override
			public void handle(Handler<R> object) {
				invoker.invoke(object);
			}

		});
		adder = new Invoker<R>() {

			@Override
			public void invoke(Handler<R> object) {
				receiver.add(new PlainInvoker<Handler<R>>(object));
			}

		};
		receiveThread = new Threadable() {

			@Override
			public void mainLoop() {
				try {
					while (isRunning()) {
						connection.receive(adder);
					}
				} catch (ConnectionClosedException cce) {
					cancel();
				} catch (Exception e) {
					e.printStackTrace();
					cancel();
				}
			}

		};
		sender = new ThreadSender<S>(connection);
	}

	// private object members

	private Connection<S, R> connection;

	private Invoker<R> invoker;

	private ThreadSender<S> sender;

	private ThreadInvoker<Handler<R>> receiver;

	private Threadable receiveThread;

	private Invoker<R> adder;

	private Runnable onDisconnect;

}
