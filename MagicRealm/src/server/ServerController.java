/**
 * This is the main controller of the server 
 * This is where the main game loop and everything that has to do with game progression is.
 */

package server;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.Semaphore;

import model.activity.Activity;
import model.activity.Move;
import model.controller.ModelController;
import model.enums.ActivityType;
import model.enums.CharacterType;
import model.enums.CounterType;
import model.enums.LandType;
import model.enums.MapChitType;
import model.enums.PhaseType;
import model.enums.TableType;
import model.enums.TileName;
import model.enums.ValleyChit;
import communication.ClientNetworkHandler;
import communication.handler.server.CheckSwordsmanPlay;
import communication.handler.server.DiceRequest;
import communication.handler.server.EnterBirdSong;
import communication.handler.server.EnterCharacterSelection;
import communication.handler.server.EnterLobby;
import communication.handler.server.RequestSearchInformation;
import communication.handler.server.SearchChoiceRequest;
import communication.handler.server.SearchResults;
import communication.handler.server.SetCheatMode;
import communication.handler.server.UpdateHiding;
import communication.handler.server.IllegalMove;
import communication.handler.server.MessageDisplay;
import communication.handler.server.Reject;
import communication.handler.server.StartGame;
import communication.handler.server.UpdateCharacterSelection;
import communication.handler.server.UpdateLobbyCount;
import communication.handler.server.UpdateLocationOfCharacter;
import communication.handler.server.serialized.SerializedBoard;
import model.character.Character;
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
			CounterType location) {
		int pos = findClient(iD);
		if (pos >= 0) {
			System.out.println("SERVER: setting client: " + iD + " character.");
			clients.get(pos).setCharacter(character, location);
			model.setPlayersInitialLocations(clients.get(pos).getCharacter()
					.getType().toCounter(), location);
		}
		boolean everyoneSelected = true;
		// wait for all clients to choose their character

		for (ClientThread client : clients) {
			System.out.println("Client request: " + iD);
			if (!client.didSelectCharacter()) {
				everyoneSelected = false;
				break;
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
		HashMap<Integer, Character> characters = new HashMap<Integer, Character>();
		for (ClientThread c : clients) {
			characters.put(c.getID(), c.getCharacter());
		}
		sendAll(new StartGame(sboard, characters));
		if(GameConfiguration.Cheat){
			sendAll(new SetCheatMode());
		}
		startBirdSong();
	}

	private void startBirdSong() {
		sendAll(new EnterBirdSong());

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
		for (ClientThread player : clients) {
			if (swordsmanPlayer != null && !swordsmanPlayer.hasPlayed()) {
				swordsmanPlayer.send(new CheckSwordsmanPlay());
				try {
					playSync.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if (player.getCharacter().getType() != CharacterType.SWORDSMAN) {
				playTurn(player);
			}
		}

		// check if swodsman played. if not, they have to play now.
		if (swordsmanPlayer != null && !swordsmanPlayer.hasPlayed()) {
			playTurn(swordsmanPlayer);
		}
		resetPlayers();
		// loop back to birdsong. BAD because recursive... TODO fix if time
		// permits.
		startBirdSong();
	}

	private void playTurn(ClientThread player) {
		player.getCharacter().setHiding(false);
		sendAll(new UpdateHiding(player.getCharacter().getType(), false));
		for (Activity act : player.getCurrentActivities()) {
			System.out.println("SERVER: current activity: " + act.getType() + " Phase: " + act.getPhaseType());
			if(model.getBoard().getLocationOfCounter(player.getCharacter().getType().toCounter()).getLandType() == LandType.CAVE){
				player.setSunlightFlag(true);
			}
			if (player.getSunlightFlag()
					&& act.getPhaseType().equals(PhaseType.SUNLIGHT) && act.getType() != ActivityType.NONE) {
				player.send(new MessageDisplay(
						"error using sunlight phase. you passed in a cave."));
			} else {
				if (player.getMountainMoveCount() != 0) {
					// this has to be a move to the same clearing as before. or
					// else send illegal move and carry on with this activity.
					System.out.println("player tried to move to mountain already.");
					if (act.getType() == ActivityType.MOVE) {
						// check if same clearing as before.
						if (!(((Move) act).getTile() == player
								.getMountainClearing().getParentTile()
								.getName()
								&& ((Move) act).getClearing() == player
										.getMountainClearing()
										.getClearingNumber())){
							player.send(new MessageDisplay("move failed. you need two moves to move to a mountain clearing."));
							player.setMountainClearing(null);
							player.setMountainMoveCount(0);
						}
					}else{
						player.send(new MessageDisplay("move failed. you need two moves to move to a mountain clearing."));
						player.setMountainClearing(null);
						player.setMountainMoveCount(0);
					}
				}
				act.perform(this);
			}
		}
		player.playTurn();
	}

	private Semaphore playSync = new Semaphore(0);
	private int currentDieRoll;

	public synchronized void setSwordsManTurn(boolean playing) {
		// swordsmanTurn = playing;
		if (playing) {
			ClientThread ct = getPlayerOf(CharacterType.SWORDSMAN);
			playTurn(ct);
		}
		playSync.release();
	}

	private void resetPlayers() {
		for (ClientThread ct : clients) {
			ct.newTurn();
		}

	}

	public void hideCharacter(CharacterType actor, int rv) {		
		if (model.hideCharacter(rv, getPlayerOf(actor).getPlayer())) {
			sendAll(new UpdateHiding(actor, true));
		} else {
			getPlayerOf(actor).send(new MessageDisplay("Hide Failed."));
		}
	}

	public void moveCharacter(CharacterType actor, TileName tile, int clearing) {
		if (model.getBoard().getClearing(tile, clearing).getLandType() == LandType.MOUNTAIN) {
			if (getPlayerOf(actor).getMountainClearing() == null) {
				getPlayerOf(actor).setMountainClearing(
						(model.getBoard().getClearing(tile, clearing)));
				getPlayerOf(actor).setMountainMoveCount(1);
				return;
			}
		}
		if (model.moveCharacter(this.getPlayerOf(actor).getPlayer(), tile,
				clearing)) {
			getPlayerOf(actor).setMountainClearing(null);
			getPlayerOf(actor).setMountainMoveCount(0);

			if (model.checkIfCave(tile, clearing)) {
				getPlayerOf(actor).setSunlightFlag(true);
			}
			sendAll(new UpdateLocationOfCharacter(actor, tile, clearing));
		} else {
			getPlayerOf(actor).send(new IllegalMove(tile, clearing));
		}
	}

	public void startSearching(CharacterType actor) {
		ClientThread ct = getPlayerOf(actor);
		ct.send(new RequestSearchInformation());
		try {
			playSync.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void searchChosen(CharacterType car, TableType tbl, int rv) {
		// do the search activity with the player
		ClientThread ct = getPlayerOf(car);
		SearchResults res = model.performSearch(ct.getPlayer(), tbl, rv);
		getPlayerOf(car).send(res);
		playSync.release();
	}

	private ClientThread getPlayerOf(CharacterType ct) {
		for (ClientThread cli : clients) {
			if (cli.getCharacter().getType() == ct) {
				return cli;
			}
		}
		throw new RuntimeException("The character " + ct
				+ " is not being played!");
	}

	public void setDieRoll(int roll) {
		currentDieRoll = roll;
		playSync.release();
	}

}