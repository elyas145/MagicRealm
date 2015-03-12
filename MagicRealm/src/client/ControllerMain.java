package client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import communication.ClientNetworkHandler;
import communication.handler.client.CharacterSelected;
import communication.handler.server.serialized.SerializedBoard;
import communication.handler.server.serialized.SerializedClearing;
import communication.handler.server.serialized.SerializedTile;
import lwjglview.controller.LWJGLViewController;
import lwjglview.graphics.LWJGLGraphics;
import lwjglview.graphics.board.LWJGLBoardDrawable;
import lwjglview.selection.SelectionFrame;
import model.activity.Activity;
import model.board.Board;
import model.board.clearing.Clearing;
import model.character.Character;
import model.character.CharacterFactory;
import model.controller.ModelControlInterface;
import model.controller.requests.DieRequest;
import model.counter.chit.MapChit;
import model.enums.CharacterType;
import model.enums.CounterType;
import model.enums.TileName;
import model.enums.ValleyChit;
import model.exceptions.MRException;
import swingview.MainView;
import utils.resources.ResourceHandler;
import view.controller.ViewController;
import view.controller.game.BoardView;
import view.controller.search.SearchView;

public class ControllerMain implements ClientController {

	private ResourceHandler rh;
	private BoardView boardView;
	private LWJGLViewController mainView;
	private Board board;
	private CharacterType player;
	private int clientID = -1;
	private ClientServer server;

	public ControllerMain() {
		rh = new ResourceHandler();
		mainView = new LWJGLViewController(rh, this);
		server = new ClientServer(this);
		goToMainMenu();
	}

	@Override
	public BoardView startBoardView() {
		try {
			mainView.startNetworkGame();
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
		mainView.enterMainMenu();
	}

	@Override
	public void exit() {
		String ObjButtons[] = { "Yes", "No" };
		int PromptResult = JOptionPane.showOptionDialog(null,
				"Are you sure you want to exit?", "Magic Realm",
				JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null,
				ObjButtons, ObjButtons[1]);
		if (PromptResult == JOptionPane.YES_OPTION) {
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
		// TODO Auto-generated method stub

	}

	@Override
	public void rollDie(CharacterType actor, DieRequest peerTable) {
		// TODO Auto-generated method stub

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
						sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
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
		initializeBoard(sboard);
	}

	@Override
	public void enterCharacterSelection() {
		System.out.println("Entered player selection.");

		// TODO for testing purposes
		characterSelected(CharacterType.AMAZON, ValleyChit.HOUSE);
		// TODO gfx.enterCharacterSelection();
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
		// TODO gfx.enterBirdSong();
	}

	/**
	 * called when the client is done putting in their moves. sends the
	 * activities to the server. client should have a message displayed saying
	 * "waiting for other players" after this method is called.
	 */
	public void endBirdSong() {

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
	public void connect(String ipaddress, int port) {

		try {
			server.connect(ipaddress, port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * updates the number of players needed to start the game.
	 * 
	 * @param count
	 */
	@Override
	public void updateLobbyCount(int count) {
		System.out.println("update lobby count called: " + count);
		// boardView.setLobbyCount(count);
	}

	@Override
	public void startGame(SerializedBoard board) {
		System.out.println("starting game.");
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
	public synchronized void setBoardView(LWJGLBoardDrawable board) {
		boardView = board;
		notify();
	}

}
