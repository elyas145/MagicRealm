package client;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import jogamp.audio.JogAmpSoundController;
import communication.ClientNetworkHandler;
import communication.handler.client.CharacterSelected;
import communication.handler.client.SearchCriteria;
import communication.handler.client.SetSwordsmanPlay;
import communication.handler.client.SubmitActivities;
import communication.handler.client.UpdateMapChitsRequest;
import communication.handler.server.serialized.SerializedBoard;
import communication.handler.server.serialized.SerializedClearing;
import communication.handler.server.serialized.SerializedMapChit;
import communication.handler.server.serialized.SerializedTile;
import config.GameConfiguration;
import config.NetworkConfiguration;
import lwjglview.controller.LWJGLViewController;
import model.activity.Activity;
import model.activity.Enchant;
import model.activity.Hide;
import model.activity.Move;
import model.activity.Search;
import model.board.Board;
import model.board.clearing.Clearing;
import model.character.Character;
import model.character.CharacterFactory;
import model.character.Phase;
import model.character.belonging.Belonging;
import model.counter.chit.MapChit;
import model.enums.ActivityType;
import model.enums.CharacterType;
import model.enums.ChitType;
import model.enums.CounterType;
import model.enums.MapChitType;
import model.enums.PhaseType;
import model.enums.SearchType;
import model.enums.TableType;
import model.enums.TileName;
import model.exceptions.MRException;
import utils.random.Random;
import utils.resources.ResourceHandler;
import view.controller.BirdsongFinishedListener;
import view.controller.BoardReadyListener;
import view.controller.ClearingSelectedListener;
import view.controller.ViewController;
import view.controller.characterselection.CharacterSelectionListener;
import view.controller.cheatmode.DieSelectionListener;
import view.controller.game.BoardView;
import view.controller.mainmenu.MenuItem;
import view.controller.mainmenu.MenuItemListener;
import view.controller.search.SearchTypeListener;
import view.controller.search.TableSelectionListener;

public class ControllerMain implements ClientController {

	private ResourceHandler rh;
	private BoardView boardView;
	private ViewController mainView;
	private Board board;
	private Map<Integer, Character> characters;
	private int clientID = -1;
	private ClientServer server;
	private int sleepTime = 2000;
	private MenuItemListener mainMenuListener;
	private ArrayList<CharacterType> disabledCharacters;
	private ArrayList<String> updateStrings;

	public ControllerMain() {
		rh = new ResourceHandler();
		mainView = new LWJGLViewController(rh, new JogAmpSoundController());
		updateStrings = new ArrayList<String>();
		server = new ClientServer(this);
		disabledCharacters = new ArrayList<CharacterType>();
		characters = new HashMap<Integer, Character>();
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
		mainView.enterMainMenu(mainMenuListener); // this is the only required
													// line
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
		mainView.revealAllMapChits(chits);
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
		if (hid) {
			boardView.hideCounter(character.toCounter());
		} else {
			boardView.unHideCounter(character.toCounter());
		}
	}

	@Override
	public void raiseException(MRException exception) {
		mainView.displayMessage(exception.getMessage());
	}

	@Override
	public void performPeerChoice() {
		// TODO perform peer choice

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
		board = new Board(sboard);
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
				tilesSet.release();
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
	public void enterLobby() {
		System.out.println("Enter lobby called.");
		mainView.enterLobby();
	}

	@Override
	public void enterCharacterSelection(ArrayList<CharacterType> disabled) {
		System.out.println("Entered player selection.");
		mainView.hideMainMenu();
		ArrayList<CharacterType> characters = new ArrayList<CharacterType>();
		for (CharacterType ct : CharacterType.values()) {
			if ((disabled != null) && !(disabled.contains(ct))) {
				characters.add(ct);
			} else {
				if (disabled == null)
					characters.add(ct);
			}
		}
		mainView.enterCharacterSelection(characters,
				new CharacterSelectionListener() {

					@Override
					public void onCharacterSelected(
							final CharacterType character) {
						waitForTiles();
						mainView.displayMessage("Please select a dwelling to start");
						for (CounterType ct : CharacterFactory
								.getPossibleStartingLocations(character)) {
							boardView.setCounter(ct, board
									.getLocationOfCounter(ct).getParentTile()
									.getName(), board.getLocationOfCounter(ct)
									.getClearingNumber());
						}
						final ArrayList<String> error = new ArrayList<String>();
						error.add("Please choose a dwelling");
						mainView.selectClearing(new ClearingSelectedListener() {

							@Override
							public void onClearingSelection(TileName tile,
									int clearing) {
								if (clearing != 0) {
									CounterType loc;
									if ((loc = board.confirmLocationOfDwelling(
											tile, clearing)) != null) {
										if (CharacterFactory
												.getPossibleStartingLocations(
														character)
												.contains(loc)) {
											characterSelected(character, loc);
										}
									} else {
										mainView.updateLog(error);
										mainView.selectClearing(this);
									}
								} else {
									mainView.selectClearing(this);
								}

							}

						});
					}

				});
	}

	/**
	 * called by the view when the client has pressed the submit button to
	 * submit their character.
	 * 
	 * @param character
	 */
	public void characterSelected(CharacterType character, CounterType loc) {
		System.out.println("character selected.");
		mainView.displayBanner("Loading, please wait...");
		if (!server.send(new CharacterSelected(clientID, character, loc))) {
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
		mainView.hideBanner();
		// phases:
		final ArrayList<Phase> phases = new ArrayList<Phase>();

		phases.add(new Phase(PhaseType.DEFAULT, characters.get(clientID)
				.getType()));
		phases.add(new Phase(PhaseType.DEFAULT, characters.get(clientID)
				.getType()));
		phases.addAll(characters.get(clientID).getSpecialPhases());
		phases.add(new Phase(PhaseType.SUNLIGHT, characters.get(clientID)
				.getType()));
		phases.add(new Phase(PhaseType.SUNLIGHT, characters.get(clientID)
				.getType()));
		mainView.enterBirdSong(1, phases, new BirdsongFinishedListener() {
			Semaphore sem = new Semaphore(0);

			@Override
			public void onFinish(List<ActivityType> activities) {
				final ArrayList<Activity> activitiesList = new ArrayList<Activity>();
				final int i[] = new int[1];
				i[0] = 0;
				System.out.println("Phases: " + phases);
				System.out.println("Activities: " + activities);
				for (ActivityType act : activities) {
					switch (act) {
					case MOVE:
						mainView.selectClearing(new ClearingSelectedListener() {

							@Override
							public void onClearingSelection(TileName tile,
									int clearing) {
								if (clearing != 0) {
									if (mainView.confirm(
											"Move to " + tile.toString() + " "
													+ clearing + "?", "Yes",
											"No")) {
										activitiesList.add(new Move(characters
												.get(clientID).getType(), tile,
												clearing, phases.get(i[0])
														.getType()));
										System.out.println("MOVE: "
												+ (i[0] + 1) + " "
												+ phases.get(i[0]));
										sem.release();
									} else {
										mainView.displayMessage("Please make a move for phase "
												+ (i[0] + 1));
										mainView.selectClearing(this);
									}
								} else {
									mainView.selectClearing(this);
								}
							}
						});
						mainView.displayMessage("Select clearing for phase "
								+ (i[0] + 1));
						try {
							sem.acquire();
							System.out.println("aquired Semaphore: "
									+ sem.availablePermits());
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

						break;
					case HIDE:
						if (!GameConfiguration.Cheat) {
							activitiesList.add(new Hide(characters
									.get(clientID).getType(), phases.get(i[0])
									.getType(), Random.dieRoll()));
						} else {
							mainView.selectDie(new DieSelectionListener() {
								@Override
								public void dieSelected(int val) {
									activitiesList.add(new Hide(characters.get(
											clientID).getType(), phases.get(
											i[0]).getType(), val));
									sem.release();
								}

							});
							try {
								sem.acquire();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}

						break;
					case SEARCH:
						activitiesList.add(new Search(characters.get(clientID)
								.getType(), phases.get(i[0]).getType()));
						break;
					case ENCHANT:
						activitiesList.add(new Enchant(characters.get(clientID)
								.getType(), phases.get(i[0]).getType()));
					default:
						break;
					}
					System.out.println("i: " + i[0]);
					i[0]++;
				}
				endBirdSong(activitiesList);
				System.out.println(activities);
			}

		});
	}

	/**
	 * called when the client is done putting in their moves. sends the
	 * activities to the server. client should have a message displayed saying
	 * "waiting for other players" after this method is called.
	 */
	public void endBirdSong(Iterable<Activity> activities) {
		mainView.displayBanner("Please wait for other players");
		server.send(new SubmitActivities(characters.get(this.clientID)
				.getType(), activities));
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
			System.out.println("Client: recieved new object: "
					+ ((ClientNetworkHandler) obj).toString());
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

	private Semaphore tilesSet = new Semaphore(0);

	private void waitForTiles() {
		sleepTime = 0; // finish placing the tiles without waiting.
		try {
			tilesSet.acquire();
			tilesSet.release();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void startGame(SerializedBoard board) {
		System.out.println("starting game.");
		waitForTiles();
		this.board = new Board(board);
		ArrayList<MapChit> chits = new ArrayList<MapChit>();
		for (SerializedMapChit name : board.getMapChitLocations().keySet()) {
			chits.add(new MapChit(name));
		}
		synchronized (boardView) {
			boardView.loadMapChits(this.board.getMapChitsToLoad());
			for (CounterType t : board.getCounterPositions().keySet()) {
				boardView.setCounter(t, board.getCounterPositions().get(t)
						.getParent(), board.getCounterPositions().get(t)
						.getNumber());
			}
			for (MapChit c : chits) {
				boardView.setMapChit(c);
				if (GameConfiguration.Cheat)
					boardView.revealMapChit(c);
			}
			for (Character c : characters.values()) {
				boardView.hideCounter(c.getType().toCounter());
			}
			// boardView.hideAllMapChits();
		}
		updateStrings.clear();
		updateStrings.add("Belongings: ");
		for (Belonging b : characters.get(clientID).getBelongings()) {
			updateStrings.add(b.toString());
		}
		mainView.updateBelongings(updateStrings);
	}

	@Override
	public void updateCharacterSelection(CharacterType character) {
		mainView.disableCharacter(character);
		disabledCharacters.add(character);
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
		characters.put(clientID, character);

	}

	@Override
	public void checkSwordsmanTurn() {
		System.out.println("recieved check swordsman object.");
		mainView.hideBanner();
		server.send(new SetSwordsmanPlay(mainView.confirm(
				"Would you like to take your turn now?", "Yes ", "No")));
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
		}
	}

	@Override
	public void setAllCharacters(Map<Integer, Character> characters) {
		for (Integer i : characters.keySet()) {
			this.characters.put(i, characters.get(i));

		}

	}

	private TableType selectedTable;
	private int goldValue;

	@Override
	public void requestSearchInformation() {
		final int rollValue[] = new int[1];
		final Semaphore sem = new Semaphore(0);
		mainView.selectSearchTable(new TableSelectionListener() {
			@Override
			public void onSelect(final TableType table) {
				selectedTable = table;
				if (GameConfiguration.Cheat) {
					final Semaphore sem2 = new Semaphore(0);
					mainView.selectDie(new DieSelectionListener() {
						@Override
						public void dieSelected(int val) {
							rollValue[0] = val;
							sem2.release();
						}
					});
					try {
						sem2.acquire();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					rollValue[0] = Random.dieRoll();
				}
				if (rollValue[0] == 1 && table != TableType.LOOT) {
					// ask for row.
					ArrayList<SearchType> types = new ArrayList<SearchType>();
					table.getSearchTypes(types);
					types.remove(SearchType.CHOICE);
					final Semaphore sem = new Semaphore(0);
					mainView.selectSearchType(types, new SearchTypeListener() {

						@Override
						public void onSearchTypeSelected(SearchType st) {
							rollValue[0] = table.getRollValue(st);
							sem.release();
						}

					});
					try {
						sem.acquire();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				sem.release();
			}
		});
		try {
			sem.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		server.send(new SearchCriteria(characters.get(clientID).getType(),
				selectedTable, rollValue[0]));
	}

	@Override
	public void requestSearchChoice(final TableType table) {
	}

	@Override
	public void peekMapChits(ArrayList<MapChit> peek) {
		if (!peek.isEmpty()) {
			updateStrings.clear();
			updateStrings.add("Peeking at map chits");
			mainView.updateLog(updateStrings);
			mainView.revealAllMapChits(peek);
		}
	}

	@Override
	public void discoverPaths(ArrayList<String> paths) {
		ArrayList<String> temp = new ArrayList<String>();
		if (!paths.isEmpty()) {
			temp.add("Discovered Paths:");
			temp.addAll(paths);
			mainView.updateLog(paths);
		}
	}

	@Override
	public void discoverChits(ArrayList<MapChit> chits) {
		ArrayList<String> updates = new ArrayList<String>();
		if (!chits.isEmpty())
			updates.add("You have discovered map chits!");
		for (MapChit c : chits) {
			boardView.revealMapChit(c);
			if (c.getType().type() == ChitType.SITE) {
				updates.add("Discovered " + c.getType());
			}
		}
		mainView.updateLog(updates);
	}

	@Override
	public void updateMapChits(MapChitType type,
			ArrayList<SerializedMapChit> smapChits) {
		ArrayList<MapChit> mapChits = new ArrayList<MapChit>();
		for (SerializedMapChit c : smapChits) {
			MapChit cm = new MapChit(c);
			cm.setTile(board.getMapChitTile(type));
			mapChits.add(cm);
			board.setLocationOfMapChit(cm, cm.getTile());
		}
		boardView.replaceMapChit(board.getMapChit(type), mapChits);
		board.removeMapChit(type);
	}

	@Override
	public void clueLost(MapChitType type) {
		if (mainView.confirm("You have discovered " + type
				+ "! Would you like to place its chits?", "Yes", "No")) {
			server.send(new UpdateMapChitsRequest(type));
		}

	}

	@Override
	public void addGold(int goldValue, MapChitType site) {
		this.goldValue += goldValue;
		updateStrings.clear();
		updateStrings.add("You looted " + site + " with " + goldValue + " gold!");
		updateStrings.add("You now have " + this.goldValue + " gold");
		mainView.updateLog(updateStrings);
	}

	@Override
	public void gameFinished(CharacterType winner, int score) {
		mainView.displayBanner(("Game Over! The winner is " + winner
				+ " with score: " + score + "!"));
	}

	@Override
	public void illegalCharacterSelection(CharacterType type) {
		mainView.displayMessage("Character already selected!");
		this.enterCharacterSelection(disabledCharacters);
	}

	@Override
	public void setEnchantedTile(TileName tile, CharacterType actor,
			boolean bool) {
		boardView.setTileEnchanted(tile, bool);
		this.updateStrings.clear();
		if (bool) {
			updateStrings.add(actor + " has enchanted " + tile);
		} else {
			updateStrings.add(actor + " has cleansed " + tile);
		}
		mainView.updateLog(updateStrings);
	}

	@Override
	public void addCharacter(int id, Character character) {
		characters.put(id, character);
		board.setLocationOfCounter(character.getType().toCounter(),
				character.getInitialLocation());
		Clearing c = board
				.getLocationOfCounter(character.getType().toCounter());
		boardView.setCounter(character.getType().toCounter(), c.getParentTile()
				.getName(), c.getNumber());
		boardView.hideCounter(character.getType().toCounter());
	}

	@Override
	public void updateLog(String string) {
		updateStrings.clear();
		updateStrings.add(string);
		mainView.updateLog(updateStrings);
		
	}

}
