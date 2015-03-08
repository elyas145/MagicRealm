/**
 * This is the main controller of the server 
 * This is where the main game loop and everything that has to do with game progression is.
 */

package server;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import communication.NetworkHandler;
import communication.handler.server.EnterCharacterSelection;
import communication.handler.server.EnterLobby;
import communication.handler.server.MessageDisplay;
import communication.handler.server.Reject;
import server.ClientThread;
import config.GameConfiguration;

public class ServerController {
	private Server server;
	private ArrayList<ClientThread> clients;
	private int clientCount = 0;

	public ServerController(Server s) {
		this.server = s;
		clients = new ArrayList<ClientThread>();
	}

	/**
	 * adds a client to the game. a client thread holds everything that has to
	 * do with that client. including the character.
	 * 
	 * @param socket
	 */
	public void addClient(Socket socket) {
		ClientThread temp = new ClientThread(server, socket);
		if (clientCount < GameConfiguration.MAX_PLAYERS) {
			try {
				temp.open();
			} catch (IOException e) {
				e.printStackTrace();
			}
			temp.start();
			temp.send(temp.getID());
			System.out.println("the player id is: " + temp.getID());
			clients.add(temp);
			clientCount++;
			if (GameConfiguration.MAX_PLAYERS - clientCount == 0) {
				// added last player.
				// enter character selection.
				sendAll(new EnterCharacterSelection());
			} else {
				temp.send(new EnterLobby(GameConfiguration.MAX_PLAYERS
						- clientCount));
			}

		} else {
			temp.send(new Reject());
			System.out.println("client rejected.");
		}
	}

	private void sendAll(NetworkHandler handler) {
		for (ClientThread client : clients) {
			client.send(handler);
		}

	}

	public void remove(int ID) {
		int pos = findClient(ID);
		if (pos >= 0) {
			ClientThread toTerminate = clients.get(pos);
			System.out.println("removing client thread " + ID + " at " + pos);
			clients.remove(pos);
			clientCount--;
			try {
				toTerminate.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			toTerminate = null;
		}

	}

	private int findClient(int ID) {
		for (int i = 0; i < clients.size(); i++) {
			if (clients.get(i).getID() == ID) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * called when a client sends something to the server.
	 * 
	 * @param iD
	 * @param input
	 */
	public void handle(Object input) {
		// ((NetworkHandler) input).handle(this);
	}
}