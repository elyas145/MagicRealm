package network;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

import utils.handler.Invoker;

public class SocketConnection<S, R> implements Connection<S, R> {

	
	// public constructors
	
	public SocketConnection(Socket sock) throws IOException {
		socket = sock;
		outStream = new ObjectOutputStream(sock.getOutputStream());
		inStream = new ObjectInputStream(sock.getInputStream());
	}
	
	
	// public Connection override methods
	
	@Override
	public void send(NetworkHandler<S> obj) throws IOException {
		outStream.writeObject(obj);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void receive(Invoker<R> invoke) throws IOException, ClassNotFoundException, ConnectionClosedException {
		try {
			invoke.invoke((NetworkHandler<R>) inStream.readObject());
		} catch(EOFException ex) {
			throw new ConnectionClosedException("There was an EOFException");
		} catch(SocketException se) {
			throw new ConnectionClosedException("The socket was closed");
		}
	}
	
	@Override
	public void close() {
		try {
			socket.close();
		} catch (IOException e) {}
	}
	
	@Override
	public String getAddress() {
		return socket.getInetAddress().getHostAddress();
	}
	
	@Override
	public int getPort() {
		return socket.getLocalPort();
	}

	
	// private object members
	
	private Socket 				socket;
	
	private ObjectOutputStream	outStream;
	
	private ObjectInputStream	inStream;
	
	
}
