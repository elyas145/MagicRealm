package model.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import client.ClientController;
import config.BoardConfiguration;
import config.GameConfiguration;
import utils.random.Random;
import utils.resources.ResourceHandler;
import utils.structures.LinkedQueue;
import utils.structures.Queue;
import utils.structures.QueueEmptyException;
import model.activity.Activity;
import model.board.Board;
import model.enums.ActivityType;
import model.enums.CharacterType;
import model.enums.ChitType;
import model.enums.CounterType;
import model.enums.MapChitType;
import model.enums.PathType;
import model.enums.PeerType;
import model.enums.PhaseType;
import model.enums.TableType;
import model.enums.TileName;
import model.enums.ValleyChit;
import model.exceptions.IllegalMoveException;
import model.exceptions.PhasesAlreadySubmitedException;
import model.interfaces.ClearingInterface;
import model.interfaces.HexTileInterface;
import model.player.Player;
import model.character.CharacterFactory;
import model.character.Character;
import model.character.Phase;
import model.controller.requests.DieRequest;
import model.counter.chit.LostSite;
import model.counter.chit.MapChit;

/*
 * Meant to be a container for the entire model
 */
public class ModelController {
	public ModelController(ResourceHandler rh) {
		this.rh = rh;
		currentDay = 1;
		sites = new ArrayList<ValleyChit>();
		
		for (ValleyChit t : ValleyChit.values()) {
			sites.add(t);
		}

		mapChits = new HashSet<MapChit>();
		for (MapChitType type : MapChitType.WARNINGS) {
			mapChits.add(new MapChit(type, 'C'));
			mapChits.add(new MapChit(type, 'W'));
			mapChits.add(new MapChit(type, 'M'));
		}

		lostCity = new LostSite(MapChitType.LOST_CITY);
		lostCastle = new LostSite(MapChitType.LOST_CASTLE);

		gameStarted = false;
	}

	// TODO belongs in ClientThread.
	
	public void setCharacterHidden(CharacterType character, boolean hid) {
		getCharacter(character).setHiding(hid);
		getClient(character).setHiding(character, hid);
	}

	// TODO belongs in ClientThread.
	/*@Override
	public void performSearch(TableType selectedTable, CharacterType chr) {
		// TODO add other search table options
		switch (selectedTable) {
		default:
			// peer table.
			peerTableSearch(chr);
		}
	}*/

	// TODO belongs in ClientThread.
	/*@Override
	public void peerChoice(PeerType choice, CharacterType actor) { // choice of
																	// peer
		if (choice == null) {
			System.out.println("PEER CHOICE WAS NULL");
			return;
		}
		switch (choice) {
		case CLUES_AND_PATHS:
		case CLUES:
			peerCP(actor);
			break;
		default:
			break;
		}

	}*/

	// TODO belongs in ClientThread.
	/*@Override
	public void hideCharacter(CharacterType actor) {
		getClient(actor).rollDie(actor, DieRequest.PEER_TABLE);
	}*/

	// TODO belongs in ClientThread.
	/*@Override
	public void setPlayerActivities(List<Activity> activities, CharacterType chr) {
		getPlayerOf(chr).setActivities(activities);
	}*/

	// TODO belongs in ClientThread.

	public void hideCharacter(int chance, CharacterType character) {
		if (chance <= 5) {
			setCharacterHidden(character, true);
		} else {
			setCharacterHidden(character, false);
		}
	}

	public void moveCharacter(CharacterType characterType, TileName tt,
			int clearing) {
		Player ct = players.get(characterType);
		ClearingInterface cl1 = board.getLocationOfCounter(characterType
				.toCounter());
		ClearingInterface cl2 = board.getClearing(tt, clearing);
		if (cl1.isConnectedTo(cl2, PathType.NORMAL)
				|| ct.hasDiscoveredPath(cl1, cl2)) {
			board.moveCharacter(characterType, tt, clearing);
			getClient(characterType).moveCounter(characterType.toCounter(), tt,
					clearing);
		} else {
			getClient(characterType).raiseException(
					new IllegalMoveException(tt, clearing, characterType));
		}
	}

	public void killCharacter(CharacterType character) {
		board.removeCharacter(character);
	}

	public Board setBoard() {
		if (board == null) {
			board = new Board(rh);
			setUpWarning();
			setUpSoundAndSite();
		}
		return board;
	}

	public void setPlayersInitialLocations() {
		// TODO get users to set locations
		for (Character c : characters.values()) {
			board.setLocationOfCounter(c.getType().toCounter(),
					GameConfiguration.INITIAL_SITE);
		}
	}

	public void setSiteLocations() {
		for (ValleyChit t : sites) {
			switch (t) {
			case CHAPEL:
				board.setLocationOfCounter(t.toCounterType(),
						TileName.AWFUL_VALLEY, 5);
				break;
			case GUARD_HOUSE:
				board.setLocationOfCounter(t.toCounterType(),
						TileName.DARK_VALLEY, 5);
				break;
			case HOUSE:
				board.setLocationOfCounter(t.toCounterType(),
						TileName.CURST_VALLEY, 5);
				break;
			case INN:
				board.setLocationOfCounter(t.toCounterType(),
						TileName.BAD_VALLEY, 5);
				break;
			default:
				break;
			}
		}
	}

	public int getNumPlayers() {
		return numPlayers;
	}

	public int getCurrentDay() {
		return currentDay;
	}

	private Map<CharacterType, List<Activity>> activities;
	private Set<CharacterType> waitingCharacters;

	// TODO belongs in clientThread
	public void setPlayerActivities(CharacterType player, List<Activity> a) {
		if (waitingCharacters.contains(player)) {
			waitingCharacters.remove(player);
			activities.put(player, a);
			if (waitingCharacters.isEmpty()) {
			}
		} else {
			getClient(player).raiseException(
					new PhasesAlreadySubmitedException());
		}
	}

	public List<Activity> getCurrentActivities() {
		try {
			return players.get(orderOfPlay.top()).getPersonalHistory()
					.getCurrentActivities();
		} catch (QueueEmptyException e) {
			throw noPlayersException;
		}
	}

	public boolean isPlayerDone() {
		return currentPlayerDone;
	}

	synchronized public void setPlayerDone() {
		currentPlayerDone = true;
		this.notify();
	}


	public Board getBoard() {
		return board;
	}

	public void newDay() {
		for (Player player : players.values()) {
			player.getPersonalHistory().newDay();
		}

	}

	// TODO belongs in clientThread
	public ArrayList<Phase> getAllowedPhases() {
		ArrayList<Phase> phases = new ArrayList<Phase>();
		phases.addAll(getInitialPhases());
		//phases.addAll(getCurrentCharacter().getSpecialPhases());
		phases.addAll(getSunlightPhases());
		return phases;
	}

	public boolean isCharacterHidden(CharacterType ct) {
		return getCharacter(ct).isHiding();
	}

	public Character getCharacter(CharacterType ct) {
		return characters.get(ct);
	}

	public void startSearching(CharacterType actor) {
		getClient(actor).startSearch(actor);
	}

	// TODO belongs in clientThread
	/*private void playActivities(CharacterType chr) {
		Player plr = getPlayerOf(chr);
		for (Activity act : plr.getPersonalHistory().getCurrentActivities()) {
			act.perform(this);
		}
	}*/

	private Collection<? extends Phase> getSunlightPhases() {
		ArrayList<Phase> init = new ArrayList<Phase>();
		for (int i = 0; i < GameConfiguration.SUNLIGHT_PHASES; i++) {
			init.add(new Phase(PhaseType.SUNLIGHT));
			init.get(i).setPossibleActivities(ActivityType.values());
		}
		return init;
	}

	private Collection<? extends Phase> getInitialPhases() {
		ArrayList<Phase> init = new ArrayList<Phase>();
		for (int i = 0; i < GameConfiguration.INITIAL_PHASES; i++) {
			init.add(new Phase(PhaseType.DEFAULT));
			init.get(i).setPossibleActivities(ActivityType.values());
		}
		return init;
	}

	private void setUpWarning() {
		ArrayList<MapChit> cave = new ArrayList<MapChit>();
		ArrayList<MapChit> woods = new ArrayList<MapChit>();
		ArrayList<MapChit> mountain = new ArrayList<MapChit>();
		for (MapChit chit : mapChits) {
			if (chit.getType().type() == ChitType.WARNING) {
				switch (chit.getIdentifier()) {
				case 'C':
					cave.add(chit);
					break;
				case 'W':
					woods.add(chit);
					break;
				case 'M':
					mountain.add(chit);
				}
			}
		}

		// set the tiles
		for (TileName tn : new TileName[] { TileName.CAVERN, TileName.CAVES,
				TileName.HIGH_PASS, TileName.BORDERLAND, TileName.RUINS }) {
			board.setLocationOfMapChit(Random.remove(cave), tn);
		}

		for (TileName tn : new TileName[] { TileName.LINDEN_WOODS,
				TileName.MAPLE_WOODS, TileName.NUT_WOODS, TileName.OAK_WOODS,
				TileName.PINE_WOODS }) {
			board.setLocationOfMapChit(Random.remove(woods), tn);
		}

		for (TileName tn : new TileName[] { TileName.CLIFF, TileName.CRAG,
				TileName.DEEP_WOODS, TileName.LEDGES, TileName.MOUNTAIN }) {
			board.setLocationOfMapChit(Random.remove(mountain), tn);
		}
	}

	private static List<Integer> makeClearings() {
		ArrayList<Integer> ret = new ArrayList<Integer>();
		for (int i = 1; i <= BoardConfiguration.MAX_CLEARINGS_IN_TILE; ++i) {
			ret.add(i);
		}
		return ret;
	}

	private void setUpSoundAndSite() {
		ArrayList<MapChit> chits = new ArrayList<MapChit>();

		// add sound and site chits to array.
		for (MapChitType chit : MapChitType.SITES) {
			MapChit mc = new MapChit(chit);
			chits.add(mc);
		}
		for (MapChitType chit : MapChitType.SOUNDS) {
			List<Integer> clears = makeClearings();
			MapChit mc;
			for (int i = 0; i < 2; ++i) {
				mc = new MapChit(chit, Random.remove(clears));
				chits.add(mc);
			}
		}
		mapChits.addAll(chits);

		// rest of chits to be split up into two groups of 4.
		// one group gets the lost castle chit, and the other gets the lost
		// city.
		ArrayList<MapChit> cityList = new ArrayList<MapChit>();
		ArrayList<MapChit> castleList = new ArrayList<MapChit>();

		for (int i = 0; i < 4; ++i) {
			cityList.add(Random.remove(chits));
			castleList.add(Random.remove(chits));
		}

		// add corresponding chits.
		cityList.add(lostCity);

		castleList.add(lostCastle);

		// each chit in city list, goes on a cave tile.
		for (TileName tn : new TileName[] { TileName.BORDERLAND,
				TileName.CAVERN, TileName.CAVES, TileName.HIGH_PASS,
				TileName.RUINS }) {
			board.setLocationOfMapChit(Random.remove(cityList), tn);
		}

		// each chit in castle list goes on mountain tile
		for (TileName tn : new TileName[] { TileName.CLIFF, TileName.CRAG,
				TileName.DEEP_WOODS, TileName.LEDGES, TileName.MOUNTAIN }) {
			board.setLocationOfMapChit(Random.remove(castleList), tn);
		}

		ArrayList<MapChit> lostCityChits = new ArrayList<MapChit>();
		ArrayList<MapChit> lostCastleChits = new ArrayList<MapChit>();
		for (int i = 0; i < 5; i++) {
			// add 5 chits to lost city.
			lostCityChits.add(Random.remove(chits));
			// add 5 chits to lost castle.
			lostCastleChits.add(Random.remove(chits));
		}
		lostCastle.setWarningAndSite(lostCastleChits);
		lostCity.setWarningAndSite(lostCityChits);
	}

	public Iterable<MapChit> getMapChits() {
		return mapChits;
	}

	/*private void peerTableSearch(CharacterType character) {
		int roll = 2;// Random.dieRoll(); TODO cheat mode
		TileName ct = getTileOf(character).getName();
		switch (roll) {
		case 1:
			getClient(character).performPeerChoice();
			break;
		case 2:
			peerCP(character);
			showMessage(character, "Found hidden paths and clues in " + ct);
			break;
		case 3:
			peerHP(character);
			showMessage(character, "Found hidden paths in " + ct);
			break;
		case 4:
			peerH(character);
			break;
		case 5:
			peerC(character);
			showMessage(character, "Found hidden clues in " + ct);
			break;
		default:
			showMessage(character, "Peer has failed");
			break;
		}
	}*/

	private ClientController getClient(CharacterType character) {
		// TODO get specific game client to character
		return playingCharacters.get(character);
	}

	/*
	private void peerC(CharacterType character) {
		// Player gets to look at the map chits in their tile.
		// they do not discover any sites, but just get to see that they are
		// there.
		// TODO clearing type affects peer
		ArrayList<MapChit> peek = new ArrayList<MapChit>();
		for (MapChit chit : mapChits) {
			if (chit.getTile() == board
					.getLocationOfCounter(
							getCurrentCharacter().getType().toCounter())
					.getParentTile().getName()) {
				peek.add(chit);
			}
		}
		getPlayerOf(character).discoverAllMapChits(peek);
		getClient(character).revealMapChits(peek);
	}*/

	/*private Player getPlayerOf(CharacterType character) {
		return players.get(character);
	}

	private void peerH(CharacterType character) {
		// TODO we don't have any enemies yet.

	}

	private void peerHP(CharacterType character) { // hidden enemies and paths
		// TODO Auto-generated method stub
		peerP(character);
	}

	// PEER SEARCH: clues and paths search
	private void peerCP(CharacterType character) {
		peerC(character);
		peerP(character);
	}

	private void peerP(CharacterType character) { // peer paths
		CounterType playerCounter = character.toCounter();
		ClearingInterface clearing = board.getLocationOfCounter(playerCounter);
		ClearingInterface source = board.getLocationOfCounter(playerCounter);
		for (ClearingInterface cl : clearing.getSurrounding(PathType.HIDDEN)) {
			getPlayerOf(character).addDiscoveredPath(source, cl);
		}
	}*/

	private HexTileInterface getTileOf(CharacterType chr) {
		return board.getLocationOfCounter(chr.toCounter()).getParentTile();
	}

	/*private TileName getCurrentTile() {
		return board
				.getLocationOfCounter(
						getCurrentCharacter().getType().toCounter())
				.getParentTile().getName();
	}*/

	private ResourceHandler rh;
	private int numPlayers = 0;
	private ArrayList<ValleyChit> sites;
	private int currentDay = 0;
	private LostSite lostCity;
	private LostSite lostCastle;

	private Board board = null;
	// private ArrayList<Character> characters = null;
	private Map<CharacterType, Character> characters;
	// private ArrayList<Player> players = null;
	private Map<CharacterType, Player> players;
	private List<CharacterType> randomOrder;
	private Queue<CharacterType> orderOfPlay;
	private boolean currentPlayerDone = false;
	private Set<MapChit> mapChits;

	private HashMap<CharacterType, ClientController> playingCharacters;

	private boolean gameStarted;

	Map<Integer, Boolean> characterSelectionMap = new HashMap<Integer, Boolean>(); // keeps
																					// track
																					// of
																					// who
																					// selected
																					// their
																					// character
																					// already.

	private static final RuntimeException noPlayersException = new RuntimeException(
			"There are no players in the queue");

}
