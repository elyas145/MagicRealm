/**
 * This is the main controller of the server 
 * This is where the main game loop and everything that has to do with game progression is.
 */

package server;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import model.controller.ModelController;
import model.enums.CharacterType;
import model.enums.MapChitType;
import model.enums.TileName;
import model.enums.ValleyChit;
import communication.ClientNetworkHandler;
import communication.ServerNetworkHandler;
import communication.handler.server.EnterCharacterSelection;
import communication.handler.server.EnterLobby;
import communication.handler.server.InitBoard;
import communication.handler.server.MessageDisplay;
import communication.handler.server.Reject;
import communication.handler.server.StartGame;
import communication.handler.server.UpdateCharacterSelection;
import communication.handler.server.UpdateLobbyCount;
import communication.handler.server.serialized.SerializedBoard;
import server.ClientThread;
import utils.resources.ResourceHandler;
import config.GameConfiguration;

public class ServerController {
	private Server server;
	private ArrayList<ClientThread> clients;
	private int clientCount = 0;
	private ModelController model;
	SerializedBoard sboard;

	public ServerController(Server s) {
		this.server = s;
		clients = new ArrayList<ClientThread>();
		model = new ModelController(new ResourceHandler());
		model.setBoard();
		// TODO testing purposes.
		model.setBoardForPlay();

		sboard = model.getBoard().getSerializedBoard();
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
			System.out.println("the player id is: " + temp.getID());
			clients.add(temp);
			clientCount++;
			temp.send(new Integer(socket.getPort()));
			if (GameConfiguration.MAX_PLAYERS - clientCount == 0) {
				// added last player.
				temp.send(new EnterLobby(sboard));
				// enter character selection.
				sendAll(new EnterCharacterSelection());
			} else {
				temp.send(new EnterLobby(sboard));
				sendAll(new UpdateLobbyCount(GameConfiguration.MAX_PLAYERS
						- clientCount));
			}

		} else {
			temp.send(new Reject());
			System.out.println("client rejected.");
		}
	}

	private void sendAll(ClientNetworkHandler handler) {
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
		if (input instanceof ServerNetworkHandler) {
			System.out.println("SERVER: recieved object from client.");
			((ServerNetworkHandler) input).handle(this);
		}

	}

	/**
	 * called when the client selects their character
	 * 
	 * @param iD
	 * @param character
	 */
	public void setCharacter(int iD, CharacterType character,
			ValleyChit startingLocation) {
		int pos = findClient(iD);
		if (pos >= 0) {
			System.out.println("SERVER: setting client: " + iD + " character.");
			clients.get(pos).setCharacter(character, startingLocation);
			model.setPlayersInitialLocations(clients.get(pos).getCharacter()
					.getType().toCounter(), startingLocation);
		}
		boolean everyoneSelected = true;
		// wait for all clients to choose their character
		for (ClientThread client : clients) {
			System.out.println("Client request: " + iD);
			if (!client.didSelectCharacter()) {
				everyoneSelected = false;
				return;
			}

		}
		if (!everyoneSelected) {
			sendAll(new UpdateCharacterSelection(character));
		} else {
			model.setBoardForPlay();
			sboard = model.getBoard().getSerializedBoard();
			startGame();
		}
	}

	public void startGame() {
		sendAll(new StartGame(sboard));
	}

	public void addTreasure(MapChitType site, TileName tile, Integer clearing) {
		model.addTreasure(site, tile, clearing);

	}
}