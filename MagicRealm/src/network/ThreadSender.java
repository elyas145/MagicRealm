package network;

import java.io.IOException;
import java.net.SocketException;

import utils.handler.Handler;
import utils.handler.PlainInvoker;
import utils.threading.ThreadInvoker;

public class ThreadSender<T> implements Sender<T> {

	// public constructors

	public ThreadSender(Sender<T> conn) {
		invoker = new ThreadInvoker<NetworkHandler<T>>(
				new Handler<NetworkHandler<T>>() {

					@Override
					public void handle(NetworkHandler<T> handler) {
						transmit(handler);
					}

				});
		connection = conn;
	}
	
	public void start() {
		invoker.start();
	}

	public void stop() {
		invoker.stop();
	}

	public void cancel() {
		invoker.cancel();
	}
	
	public void join() {
		invoker.join();
	}

	// public Sender override

	@Override
	public void send(NetworkHandler<T> obj) {
		try {
			invoker.add(new PlainInvoker<NetworkHandler<T>>(obj));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// protected setter

	protected void setSender(Sender<T> conn) {
		connection = conn;
	}

	// private send method

	private void transmit(NetworkHandler<T> object) {
		try {
			connection.send(object);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// private members

	private Sender<T> connection;

	private ThreadInvoker<NetworkHandler<T>> invoker;
	
}
