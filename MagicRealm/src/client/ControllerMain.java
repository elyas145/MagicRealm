package client;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import communication.ClientNetworkHandler;
import communication.handler.client.CharacterSelected;
import communication.handler.client.SubmitActivities;
import communication.handler.server.serialized.SerializedBoard;
import communication.handler.server.serialized.SerializedClearing;
import communication.handler.server.serialized.SerializedTile;
import config.NetworkConfiguration;
import lwjglview.controller.LWJGLViewController;
import model.activity.Activity;
import model.board.Board;
import model.board.clearing.Clearing;
import model.character.Character;
import model.character.CharacterFactory;
import model.controller.requests.DieRequest;
import model.counter.chit.MapChit;
import model.enums.CharacterType;
import model.enums.CounterType;
import model.enums.TileName;
import model.enums.ValleyChit;
import model.exceptions.MRException;
import utils.resources.ResourceHandler;
import view.controller.BoardReadyListener;
import view.controller.ViewController;
import view.controller.characterselection.CharacterSelectionListener;
import view.controller.game.BoardView;
import view.controller.mainmenu.MenuItem;
import view.controller.mainmenu.MenuItemListener;

public class ControllerMain implements ClientController {

	private ResourceHandler rh;
	private BoardView boardView;
	private ViewController mainView;
	private Board board;
	private Character character;
	private int clientID = -1;
	private ClientServer server;
	private int sleepTime = 2000;

	private MenuItemListener mainMenuListener;

	public ControllerMain() {
		rh = new ResourceHandler();
		mainView = new LWJGLViewController(rh);

		server = new ClientServer(this);

		mainMenuListener = new MenuItemListener() {

			@Override
			public void onItemSelect(MenuItem item) {
				switch (item) {
				case START_GAME:
					startNetworkGame();
					break;
				case EXIT:
					exit();
					break;
				}
			}

		};

		goToMainMenu();
	}

	@Override
	public BoardView startBoardView() {
		try {
			// mainView.startNetworkGame();
			synchronized (this) {
				while (boardView == null) {
					wait();
				}
			}
			return boardView;
		} catch (Exception e1) {
			throw new RuntimeException(e1);
		}
	}

	public ArrayList<Character> getAllCharacters() {
		return CharacterFactory.getPossibleCharacters();
	}

	public ResourceHandler getRh() {
		return rh;
	}

	/**
	 * called when the client launches the game (controller constructor)
	 */
	public void goToMainMenu() {
		mainView.enterMainMenu(mainMenuListener); // this is the only required line
	}

	@Override
	public void exit() {
		if (mainView.confirm("Do you really want to exit?", "Yes", "No")) {
			System.exit(0);
		}
	}

	/*
	 * private void sendActivities(List<Activity> activities) {
	 * model.setPlayerActivities(activities, player); }
	 * 
	 * private void beginBoardTurn(Player plr) {
	 * boardView.focusOn(plr.getCharacter().getType().toCounter());
	 * boardView.hideAllMapChits(); }
	 * 
	 * private void startBirdSong(Player player, int day, List<Phase> phases,
	 * Map<TileName, List<Integer>> tileClrs) {
	 * mainView.enterBirdSong(player.getCharacter().getType(), day, phases,
	 * player.getPersonalHistory(), tileClrs); }
	 */

	/**
	 * Displays the given message on the client's GUI
	 * 
	 * @param message
	 * 
	 */
	@Override
	public void displayMessage(String string) {
		mainView.displayMessage(string);
	}

	/**
	 * Reveals the map chits given on the board.
	 * 
	 * @param chits
	 */
	@Override
	public void revealMapChits(Iterable<MapChit> chits) {

		boardView.revealAllMapChits(chits);
	}

	/**
	 * 
	 */
	@Override
	public void focusOnCharacter(CharacterType character) {
		focusOnBoard(character.toCounter());
	}

	@Override
	public void moveCounter(CounterType counter, TileName tt, int clearing) {
		boardView.setCounter(counter, tt, clearing);
	}

	@Override
	public void startSearch(CharacterType searcher) {
		/*
		 * SearchView sv = mainView.enterSearchView(searcher); synchronized (sv)
		 * { while (!sv.doneSearching()) { try { sv.wait(); } catch
		 * (InterruptedException e) { } }
		 * model.performSearch(sv.getSelectedTable(), searcher); }
		 */
	}

	@Override
	public void focusOnBoard(TileName tile) {
		boardView.focusOn(tile);
	}

	@Override
	public void focusOnBoard(TileName tile, int clearing) {
		boardView.focusOn(tile, clearing);
	}

	@Override
	public void focusOnBoard(CounterType counter) {
		boardView.focusOn(counter);
	}

	@Override
	public void setHiding(CharacterType character, boolean hid) {

	}

	@Override
	public void raiseException(MRException exception) {
		mainView.displayMessage(exception.getMessage());
	}

	@Override
	public void performPeerChoice() {
		// TODO perform peer choice

	}

	@Override
	public void rollDie(CharacterType actor, DieRequest peerTable) {
		// TODO rollDie

	}

	/**
	 * This function is called when the EnterLobby object is passed to the
	 * client.
	 * 
	 * @param sboard
	 */
	@Override
	public void initializeBoard(final SerializedBoard sboard) {
		// show the board view.
		boardView = this.startBoardView();
		/**
		 * This function runs as a thread, adding one tile to the board view at
		 * a time.
		 * 
		 * needs to be a seperate thread so this class can still handle sent
		 * objects from the server
		 */
		Thread t = new Thread() {
			@Override
			public void run() {
				for (SerializedTile tile : sboard.getsMapOfTiles().values()) {
					Map<Integer, Clearing> clearings = new HashMap<Integer, Clearing>();
					for (SerializedClearing clearing : tile.getClearings()
							.values()) {
						clearings.put(clearing.getNumber(), new Clearing(
								clearing));
					}
					boardView.setTile(tile.getName(), tile.getRow(),
							tile.getColumn(), tile.getRotation(),
							clearings.values());
					try {
						sleep(sleepTime);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				// TODO displays the dwellings for the character to pick a start
				// location.
				/*
				 * Map<CounterType, SerializedClearing> temp =
				 * sboard.getCounterPositions(); for(CounterType counter :
				 * temp.keySet()){ boardView.setCounter(counter,
				 * temp.get(counter).getParent(),
				 * temp.get(counter).getNumber()); }
				 */
			}
		};
		t.start();
	}

	@Override
	public void setPlayerActivities(CharacterType character,
			List<Activity> activities) {
		// model.setPlayerActivities(activities, character);
	}

	/**
	 * tells the gui to enter the lobby.
	 * 
	 * GUI should display how many more players we need.
	 * 
	 * GUI should display the board.
	 * 
	 */
	@Override
	public void enterLobby(SerializedBoard sboard) {
		System.out.println("Enter lobby called.");
		mainView.enterLobby();
		initializeBoard(sboard);
	}

	@Override
	public void enterCharacterSelection() {
		System.out.println("Entered player selection.");

		// TODO for testing purposes
		characterSelected(CharacterType.AMAZON, ValleyChit.HOUSE);
	}

	/**
	 * called by the view when the client has pressed the submit button to
	 * submit their character.
	 * 
	 * @param character
	 */
	public void characterSelected(CharacterType character, ValleyChit location) {
		System.out.println("character selected.");
		if (!server.send(new CharacterSelected(clientID, character, location))) {
			System.out.println("failed to send selected character to server.");
		}
	}

	/**
	 * 
	 * Enters bird song. client can select phases. graphics should call
	 * endBirdSong() when the client is done.
	 */
	@Override
	public void enterBirdSong() {
		System.out.println("Entering bird song.");
		// TODO mainView.enterBirdSong(type, day, phases, personalHistory,
		// tileClrs);
	}

	/**
	 * called when the client is done putting in their moves. sends the
	 * activities to the server. client should have a message displayed saying
	 * "waiting for other players" after this method is called.
	 */
	public void endBirdSong(Iterable<Activity> activities) {
		server.send(new SubmitActivities(this.clientID, activities));
	}

	/**
	 * sets the client's id to the given parameter
	 * 
	 * called by a network handler sent from the server.
	 */
	@Override
	public void setID(int id) {
		clientID = id;

	}

	/**
	 * called when the client receives an object from the server.
	 * 
	 * @param obj
	 */
	public void handle(Object obj) {
		if (obj instanceof Integer) {
			// ID
			setID((Integer) obj);
		}
		if (obj instanceof ClientNetworkHandler) {
			System.out.println("Client: recieved new object.");
			((ClientNetworkHandler) obj).handle(this);
		}
	}

	@Override
	public void connect(String ipaddress, int port)
			throws UnknownHostException, IOException {

		server.connect(ipaddress, port);
	}

	/**
	 * updates the number of players needed to start the game.
	 * 
	 * @param count
	 */
	@Override
	public void updateLobbyCount(int count) {
		System.out.println("update lobby count called: " + count);
		mainView.waitingForPlayers(count);
	}

	@Override
	public void startGame(SerializedBoard board) {
		System.out.println("starting game.");
		sleepTime = 0; // finish placing the tiles without waiting.
		ArrayList<MapChit> chits = new ArrayList<MapChit>();
		for (TileName name : board.getMapChitLocations().keySet()) {
			chits.add(new MapChit(board.getMapChitLocations().get(name)));
		}
		boardView.loadMapChits(chits);
		// TODO boardView.EnterGameView();

	}

	@Override
	public void updateCharacterSelection(CharacterType character) {
		// TODO mainView.updateCharacterSelection(character);
		System.out.println("Client " + clientID
				+ ": this character is now not selectable: "
				+ character.toString());
	}

	@Override
	public synchronized void setBoardView(BoardView board) {
		boardView = board;
		notify();
	}

	@Override
	public void setCharacter(Character character) {
		this.character = character;

	}

	@Override
	public void checkSwordsmanTurn() {
		mainView.displayMessage("Would you like to take your turn now?");
	}

	private void startNetworkGame() {
		try {
			connect(NetworkConfiguration.DEFAULT_IP,
					NetworkConfiguration.DEFAULT_PORT);
			mainView.startGame(new BoardReadyListener() {

				@Override
				public void boardReady(BoardView bv) {
					setBoardView(bv);
				}

			});
		} catch (Exception e) {
			e.printStackTrace();
			mainView.displayMessage("The server is not available");
			mainView.enterMainMenu(mainMenuListener);
		}
	}

}
