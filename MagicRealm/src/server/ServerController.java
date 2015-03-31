/**
 * This is the main controller of the server 
 * This is where the main game loop and everything that has to do with game progression is.
 */

package server;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.Semaphore;

import model.activity.Activity;
import model.controller.ModelController;
import model.enums.CharacterType;
import model.enums.MapChitType;
import model.enums.TableType;
import model.enums.TileName;
import model.enums.ValleyChit;
import communication.ClientNetworkHandler;
import communication.ServerNetworkHandler;
import communication.handler.server.CheckSwordsmanPlay;
import communication.handler.server.EnterCharacterSelection;
import communication.handler.server.EnterLobby;
import communication.handler.server.IllegalMove;
import communication.handler.server.InitBoard;
import communication.handler.server.MessageDisplay;
import communication.handler.server.Reject;
import communication.handler.server.StartGame;
import communication.handler.server.UpdateCharacterSelection;
import communication.handler.server.UpdateLobbyCount;
import communication.handler.server.UpdateLocationOfCharacter;
import communication.handler.server.serialized.SerializedBoard;
import server.ClientThread;
import utils.resources.ResourceHandler;
import config.GameConfiguration;

public class ServerController {
	private Server server;
	private ArrayList<ClientThread> clients;
	private int clientCount = 0;
	private ModelController model;
	private SerializedBoard sboard;
	private boolean swordsmanTurn = false;

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

	public void addTreasure(MapChitType site, TileName tile, Integer value) {
		model.addTreasure(site, tile, value);

	}

	public void addSound(MapChitType sound, TileName tile, Integer clearing) {
		model.addSound(sound, tile, clearing);
	}

	public void addWarning(MapChitType type, TileName tile) {
		model.addWarning(type, tile);

	}

	public void submitActivities(int id, Iterable<Activity> activities) {
		clients.get(findClient(id)).setCurrentActivities(activities);
		boolean done = true;
		// check if birdsong is done.
		for (ClientThread client : clients) {
			if (client.getCurrentActivities() == null) {
				done = false;
				return;
			}
		}
		if (done) {
			// start playing the phases. (daylight)
			startDayLight();
		}
	}

	private void startDayLight() {
		ClientThread swordsmanPlayer = null;
		Collections.shuffle(clients);
		// check if a client has a swordsman.
		for (ClientThread client : clients) {
			if (client.getCharacter().getType() == CharacterType.SWORDSMAN) {
				swordsmanPlayer = client;
				break;
			}
		}
		// check if the swords man wants to play.
		for (ClientThread player : clients){
			if (swordsmanPlayer != null && !swordsmanPlayer.hasPlayed()) {
				swordsmanPlayer.send(new CheckSwordsmanPlay());
				try {
					playSync.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if(player.getCharacter().getType() != CharacterType.SWORDSMAN){
				playTurn(player);
			}
		}
		
		//check if swodsman played. if not, they have to play now.
		if(swordsmanPlayer != null && !swordsmanPlayer.hasPlayed()){
			playTurn(swordsmanPlayer);
		}
	}
	
	private void playTurn(ClientThread player) { //TODO this
		for(Activity act: player.getCurrentActivities()) {
			act.perform(this);
		}
		player.playTurn();
	}

	private Semaphore playSync = new Semaphore(0);

	public synchronized void setSwordsManTurn(boolean playing) {
		//swordsmanTurn = playing;
		if (playing) {
			ClientThread ct = getPlayerOf(CharacterType.SWORDSMAN);
			playTurn(ct);
		}
		playSync.release();
	}

	public void hideCharacter(CharacterType actor) {
		model.hideCharacter(5, getPlayerOf(actor).getPlayer());
	}

	public void moveCharacter(CharacterType actor, TileName tile, int clearing) {
		if (model.moveCharacter(this.getPlayerOf(actor).getPlayer(), tile, clearing)){
			sendAll(new UpdateLocationOfCharacter(actor, tile, clearing));
		}else{
			getPlayerOf(actor).send(new IllegalMove(tile, clearing));
		}
	}

	public void startSearching(CharacterType actor) {
		ClientThread ct = getPlayerOf(actor);
		ct.send(null); // TODO send a search initiated handler
		try {
			playSync.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void searchChosen(CharacterType car, TableType tbl, int rv) {
		// TODO do the search activity with the player
		ClientThread ct = getPlayerOf(car);
		playSync.release();
	}
	
	private ClientThread getPlayerOf(CharacterType ct) {
		for(ClientThread cli: clients) {
			if(cli.getCharacter().getType() == ct) {
				return cli;
			}
		}
		throw new RuntimeException("The character " + ct + " is not being played!");
	}
	
}