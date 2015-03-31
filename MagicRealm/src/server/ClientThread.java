package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import model.enums.CharacterType;
import model.enums.CounterType;
import model.enums.ValleyChit;
import model.activity.Activity;
import model.character.Character;
import model.character.CharacterFactory;
public class ClientThread extends Thread {
	private int ID = -1;
	private Socket socket = null;
	private Server server = null;
	private ObjectInputStream oStreamIn = null;
	private ObjectOutputStream oStreamOut = null;
	private boolean done = false;
	private Character character;
	private boolean characterPicked = false;
	private Iterable<Activity> currentActivities;
	private boolean playedTurn;
	
	public ClientThread(Server server, Socket socket) {
		super();
		this.server = server;
		this.socket = socket;
		this.ID = socket.getPort();
		playedTurn = false;
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
			System.out.println(ID + "Error sending message: " + ID);
			e.printStackTrace();
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
				// e.printStackTrace();
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

	public void setCharacter(CharacterType character, ValleyChit location) {
		this.character = CharacterFactory.create(character, location);
		characterPicked = true;
	}

	public boolean didSelectCharacter() {
		return this.characterPicked;
	}

	public Character getCharacter() {
		return character;
	}

	public void setCurrentActivities(Iterable<Activity> activities) {
		currentActivities = activities;		
	}

	public Iterable<Activity> getCurrentActivities() {
		// TODO Auto-generated method stub
		return currentActivities;
	}
	
	public void playTurn() {
		playedTurn = true;
	}
	
	public void newTurn() {
		playedTurn = false;
	}
	
	public boolean hasPlayed() {
		return playedTurn;
	}
	
}
