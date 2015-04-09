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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Semaphore;

import model.activity.Activity;
import model.activity.Move;
import model.controller.ModelController;
import model.counter.chit.LostSite;
import model.counter.chit.MapChit;
import model.enums.ActivityType;
import model.enums.CharacterType;
import model.enums.CounterType;
import model.enums.LandType;
import model.enums.MapChitType;
import model.enums.PhaseType;
import model.enums.TableType;
import model.enums.TileName;
import communication.ClientNetworkHandler;
import communication.handler.server.CheckSwordsmanPlay;
import communication.handler.server.EnterBirdSong;
import communication.handler.server.EnterCharacterSelection;
import communication.handler.server.EnterLobby;
import communication.handler.server.GameFinished;
import communication.handler.server.IllegalCharacterSelection;
import communication.handler.server.PlayerAdded;
import communication.handler.server.RequestSearchInformation;
import communication.handler.server.SearchResults;
import communication.handler.server.SetBoard;
import communication.handler.server.SetCheatMode;
import communication.handler.server.UpdateEnchantedTile;
import communication.handler.server.UpdateHiding;
import communication.handler.server.IllegalMove;
import communication.handler.server.MessageDisplay;
import communication.handler.server.Reject;
import communication.handler.server.StartGame;
import communication.handler.server.UpdateCharacterSelection;
import communication.handler.server.UpdateLobbyCount;
import communication.handler.server.UpdateLocationOfCharacter;
import communication.handler.server.UpdateMapChits;
import communication.handler.server.serialized.SerializedBoard;
import communication.handler.server.serialized.SerializedMapChit;
import model.character.Character;
import server.ClientThread;
import utils.resources.ResourceHandler;
import config.GameConfiguration;
import config.NetworkConfiguration;

public class ServerController {
	private Server server;
	private Map<Integer, ClientThread> current;
	private Set<Integer> joined;
	private Set<Integer> waiting;
	private Set<Integer> playing;
	private Set<Integer> observing;
	private ModelController model;
	private SerializedBoard sboard;
	private int currentDay = 0;
	private Set<CharacterType> disabledCharacters;
	private HashMap<Integer, Character> characters;
	private ServerState state;

	public ServerController(Server s) {
		this.server = s;
		current = new HashMap<Integer, ClientThread>();
		joined = new HashSet<Integer>();
		waiting = new HashSet<Integer>();
		playing = new HashSet<Integer>();
		observing = new HashSet<Integer>();
		model = new ModelController(new ResourceHandler());
		model.setBoard();
		disabledCharacters = new HashSet<CharacterType>();
		state = ServerState.IDLE;
		characters = new HashMap<Integer, Character>();
		if (NetworkConfiguration.START_NORMAL)
			sboard = model.getBoard().getSerializedBoard();
		else {
			model.setBoardForPlay();
			sboard = model.getBoard().getSerializedBoard();
		}
	}

	/**
	 * adds a client to the game. a client thread holds everything that has to
	 * do with that client. including the character.
	 * 
	 * @param socket
	 */
	public void addClient(Socket socket) {
		ClientThread temp = new ClientThread(server, socket);
		if (countClients() < GameConfiguration.MAX_PLAYERS) {
			try {
				temp.open();
			} catch (IOException e) {
				e.printStackTrace();
			}
			temp.start();
			System.out.println("the player id is: " + temp.getID());
			justJoined(temp);
			temp.send(new Integer(socket.getPort()));
			temp.send(new SetBoard(sboard));
			if (NetworkConfiguration.START_NORMAL) {
				temp.send(new EnterLobby());
				if (GameConfiguration.MAX_PLAYERS == countClients()) {
					// added last player.
					// enter character selection.
					sendAll(new EnterCharacterSelection(), joined);
				} else {
					sendAll(new UpdateLobbyCount(GameConfiguration.MAX_PLAYERS
							- countClients()), joined);
				}

			} else {
				// temp.send(new SetBoard(sboard));
				temp.send(new EnterCharacterSelection(
						new ArrayList<CharacterType>(disabledCharacters)));
			}
		} else {
			temp.send(new Reject());
			System.out.println("client rejected.");
		}
	}

	private void justJoined(ClientThread cli) {
		int id = cli.getID();
		current.put(id, cli);
		joined.add(id);
	}

	private void startWaiting(int id) {
		synchronized (waiting) {
			waiting.add(id);
		}
		observing.add(id);
		joined.remove(id);
	}

	private void startPlaying(int id) {
		playing.add(id);
		observing.add(id);
		synchronized (waiting) {
			waiting.remove(id);
		}
	}

	private ClientThread getClient(int id) {
		return current.get(id);
	}

	private void sendAll(ClientNetworkHandler handler, Iterable<Integer> ids) {
		for (int client : ids) {
			send(client, handler);
		}
	}

	private void send(int cl, ClientNetworkHandler handle) {
		getClient(cl).send(handle);
	}

	public void remove(int ID) {
		joined.remove(ID);
		synchronized (waiting) {
			waiting.remove(ID);
		}
		observing.remove(ID);
		playing.remove(ID);
		ClientThread term = getClient(ID);
		try {
			term.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private int countClients() {
		return current.size();
	}

	/**
	 * called when the client selects their character
	 * 
	 * @param iD
	 * @param character
	 */
	public void setCharacter(int iD, CharacterType character,
			CounterType location) {

		ClientThread clt = getClient(iD);
		if (disabledCharacters.contains(character)) {
			clt.send(new IllegalCharacterSelection(character));
			return;
		}
		System.out.println("SERVER: setting client: " + iD + " character.");
		clt.setCharacter(character, location);
		CharacterType ct = character;
		model.setPlayersInitialLocations(ct.toCounter(), location);
		disabledCharacters.add(character);
		sendAll(new UpdateCharacterSelection(clt.getCharacterType()), joined);
		sendAll(new PlayerAdded(iD, clt.getCharacter()), observing);
		
		for (Character chr : characters.values()) {
			send(iD,
					new UpdateLocationOfCharacter(chr.getType(), model
							.getTile(chr), model.getClearing(chr)));
			send(iD, new UpdateHiding(chr.getType(), chr.isHiding()));
		}
		characters.put(iD, clt.getCharacter());
		startWaiting(iD);
		if (NetworkConfiguration.START_NORMAL) {
			boolean everyoneSelected = true;
			// wait for all clients to choose their character

			for (ClientThread client : current.values()) {
				System.out.println("Client request: " + iD);
				if (!client.didSelectCharacter()) {
					everyoneSelected = false;
					break;
				}

			}
			if (!everyoneSelected) {
				synchronized (waiting) {
					sendAll(new UpdateCharacterSelection(character), waiting);
				}
			} else {
				if (GameConfiguration.Cheat) {
					sendAll(new SetCheatMode(), waiting);
				}
				startGame();
			}
		} else {
			if (playing.size() == 0) {
				startGame();
			} else {
				sboard = model.getBoard().getSerializedBoard();
				clt.send(new StartGame(sboard, characters));
				if (state == ServerState.BIRD_SONG) {
					clt.send(new EnterBirdSong());
					startPlaying(iD);
				} else {
					clt.send(new MessageDisplay(
							"Please wait for next bird song."));
				}
			}
		}
	}

	/**
	 * called when the client selects their character
	 * 
	 * @param iD
	 * @param character
	 */
	/*
	 * public void setCharacter(int iD, CharacterType character, CounterType
	 * location) { if (character == null || location == null || iD == 0) return;
	 * 
	 * boolean flag = false; for (ClientThread c : clients) { if
	 * (c.getCharacterType() == character) { flag = true; } }
	 * 
	 * int pos = findClient(iD); if (flag) { if (pos >= 0) { if
	 * (NetworkConfiguration.START_NORMAL) clients.get(pos).send( new
	 * IllegalCharacterSelection(character)); else waiting.get(pos).send( new
	 * IllegalCharacterSelection(character)); return; } else { return; } } if
	 * (pos >= 0) { System.out.println("SERVER: setting client: " + iD +
	 * " character."); if (NetworkConfiguration.START_NORMAL) {
	 * clients.get(pos).setCharacter(character, location);
	 * model.setPlayersInitialLocations(clients.get(pos).getCharacter()
	 * .getType().toCounter(), location); disabledCharacters.add(character);
	 * characters.put(iD, clients.get(pos).getCharacter()); } else {
	 * waiting.get(pos).setCharacter(character, location);
	 * model.setPlayersInitialLocations(waiting.get(pos).getCharacter()
	 * .getType().toCounter(), location); disabledCharacters.add(character);
	 * characters.put(iD, waiting.get(pos).getCharacter()); } } else { return; }
	 * if (NetworkConfiguration.START_NORMAL) { boolean everyoneSelected = true;
	 * // wait for all clients to choose their character
	 * 
	 * for (ClientThread client : clients) {
	 * System.out.println("Client request: " + iD); if
	 * (!client.didSelectCharacter()) { everyoneSelected = false; break; }
	 * 
	 * } if (!everyoneSelected) { sendAll(new
	 * UpdateCharacterSelection(character)); } else { if
	 * (GameConfiguration.Cheat) { sendAll(new SetCheatMode()); }
	 * model.setBoardForPlay(); sboard = model.getBoard().getSerializedBoard();
	 * startGame(); } } else { if (clients.size() == 0) {
	 * clients.addAll(waiting); waiting.clear(); startGame(); } else { sboard =
	 * model.getBoard().getSerializedBoard(); ClientThread cl =
	 * waiting.get(pos); cl.send(new StartGame(sboard, characters)); sendAll(new
	 * PlayerAdded(iD, cl.getCharacter())); if (state == ServerState.BIRD_SONG)
	 * { cl.send(new EnterBirdSong()); clients.addAll(waiting); waiting.clear();
	 * } else { cl.send(new MessageDisplay( "Please wait for next bird song."));
	 * } } } }
	 */

	public void startGame() {
		model.setBoardForPlay();
		sboard = model.getBoard().getSerializedBoard();
		sendAll(new StartGame(sboard, characters), waiting);
		startBirdSong();
	}

	private void startBirdSong() {
		state = ServerState.BIRD_SONG;
		synchronized (waiting) {
			for (int id : waiting) {
				playing.add(id);
				observing.add(id);
			}
		}
		waiting.clear();
		if (currentDay == GameConfiguration.LUNAR_MONTH) {
			ClientThread winner = null;
			synchronized (playing) {
				for (int id : playing) {
					ClientThread t = getClient(id);
					if (winner == null)
						winner = t;
					else
						winner = winner.getPlayer().getGold() < t.getPlayer()
								.getGold() ? t : winner;
				}
			}
			sendAll(new GameFinished(winner.getCharacter().getType(), winner
					.getPlayer().getGold()), current.keySet());
		} else {
			currentDay++;
			sendAll(new EnterBirdSong(), playing);
		}
	}

	public void addSite(MapChitType site, TileName tile) {
		model.addSite(site, tile);

	}

	public void addSound(MapChitType sound, TileName tile) {
		model.addSound(sound, tile);
	}

	public void addWarning(MapChitType type, TileName tile) {
		model.addWarning(type, tile);

	}

	public void submitActivities(CharacterType type,
			Iterable<Activity> activities) {
		getPlayerOf(type).setCurrentActivities(activities);
		boolean done = true;
		// check if birdsong is done.
		synchronized (playing) {
			for (int client : playing) {
				if (getClient(client).getCurrentActivities() == null) {
					done = false;
					return;
				}
			}
		}
		if (done) {
			// start playing the phases. (daylight)
			startDayLight();
		}
	}
	private Semaphore turnSem = new Semaphore(1);
	private void startDayLight() {
		state = ServerState.DAYLIGHT;
		ClientThread swordsmanPlayer = null;
		ArrayList<ClientThread> cls = new ArrayList<ClientThread>();
		// check if a client has a swordsman.
		synchronized (playing) {
			for (int cl : playing) {
				ClientThread client = getClient(cl);
				if (client.getCharacter().getType() == CharacterType.SWORDSMAN) {
					swordsmanPlayer = client;
				} else {
					cls.add(client);
				}
			}
		}
		Collections.shuffle(cls);
		// check if the swords man wants to play.
		for (ClientThread player : cls) {
			try {
				turnSem.acquire();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if (swordsmanPlayer != null && !swordsmanPlayer.hasPlayed()) {
				swordsmanPlayer.send(new CheckSwordsmanPlay());
				try {
					playSync.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			playTurn(player);
			turnSem.release();
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
		sendAll(new UpdateHiding(player.getCharacter().getType(), false),
				playing);
		for (Activity act : player.getCurrentActivities()) {
			System.out.println("SERVER: current activity: " + act.getType()
					+ " Phase: " + act.getPhaseType());
			if (model
					.getBoard()
					.getLocationOfCounter(
							player.getCharacter().getType().toCounter())
					.getLandType() == LandType.CAVE) {
				player.setSunlightFlag(true);
			}
			if (player.getSunlightFlag()
					&& act.getPhaseType().equals(PhaseType.SUNLIGHT)
					&& act.getType() != ActivityType.NONE) {
				player.send(new MessageDisplay(
						"error using sunlight phase. you passed in a cave."));
			} else {
				if (player.getMountainMoveCount() != 0) {
					// this has to be a move to the same clearing as before. or
					// else send illegal move and carry on with this activity.
					System.out
							.println("player tried to move to mountain already.");
					if (act.getType() == ActivityType.MOVE) {
						// check if same clearing as before.
						if (!(((Move) act).getTile() == player
								.getMountainClearing().getParentTile()
								.getName() && ((Move) act).getClearing() == player
								.getMountainClearing().getClearingNumber())) {
							player.send(new MessageDisplay(
									"move failed. you need two moves to move to a mountain clearing."));
							player.setMountainClearing(null);
							player.setMountainMoveCount(0);
						}
					} else {
						player.send(new MessageDisplay(
								"move failed. you need two moves to move to a mountain clearing."));
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
	public void setSwordsManTurn(boolean playing) {
		// swordsmanTurn = playing;
		if (playing) {
			ClientThread ct = getPlayerOf(CharacterType.SWORDSMAN);
			playTurn(ct);
		}
		playSync.release();
	}

	private void resetPlayers() {
		synchronized (playing) {
			for (int ct : playing) {
				getClient(ct).newTurn();
			}
		}
	}

	public void hideCharacter(CharacterType actor, int rv) {
		if (model.hideCharacter(rv, getPlayerOf(actor).getPlayer())) {
			sendAll(new UpdateHiding(actor, true), playing);
		} else {
			getPlayerOf(actor).send(new MessageDisplay("Hide Failed."));
		}
	}

	public void moveCharacter(CharacterType actor, TileName tile,
			int clearing) {
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
			sendAll(new UpdateLocationOfCharacter(actor, tile, clearing),
					playing);
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

	public void searchChosen(CharacterType car, TableType tbl,
			int rv) {
		// do the search activity with the player
		ClientThread ct = getPlayerOf(car);
		SearchResults res = model.performSearch(ct.getPlayer(), tbl, rv);
		if (tbl == TableType.LOCATE) {
			if (res.isCastle() || res.isCity()) {
				LostSite ls;
				if (res.isCastle()) {
					ls = model.getCastle();
				} else {
					ls = model.getCity();
				}
				model.getBoard().removeMapChit(ls);
				for (MapChit c : model.getCastle().getWarningAndSite()) {
					model.getBoard().setLocationOfMapChit(c, ls.getTile());
				}
				ArrayList<SerializedMapChit> smapchits = new ArrayList<SerializedMapChit>();
				for (MapChit c : ls.getWarningAndSite()) {
					smapchits.add(c.getSerializedChit());
				}
				sendAll(new UpdateMapChits(ls.getType(), smapchits), observing);
			}
		}
		getPlayerOf(car).send(res);
		playSync.release();
	}

	private ClientThread getPlayerOf(CharacterType ct) {
		synchronized (playing) {
			for (int cl : playing) {
				ClientThread cli = getClient(cl);
				if (cli.getCharacter().getType() == ct) {
					return cli;
				}
			}
		}
		throw new RuntimeException("The character " + ct
				+ " is not being played!");
	}

	public void updateMapChits(MapChitType type) {
		switch (type) {
		case LOST_CASTLE:
			model.setLostCastleFound(true);
		case LOST_CITY:
			model.setLostCityFound(true);
		default:
			break;
		}
		sendAll(new UpdateMapChits(type, model.getLostChits(type)), observing);
	}

	public void setLost(MapChitType lost,
			ArrayList<MapChitType> array, TileName tile) {
		model.setLost(lost, array, tile);

	}

	public void requestEnchant(CharacterType actor) {
		sendAll(new UpdateEnchantedTile(model.getTile(getPlayerOf(actor)
				.getCharacter()), actor, model.enchantTile(getPlayerOf(actor)
				.getPlayer())), observing);
	}

}