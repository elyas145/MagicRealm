package model.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import communication.ClientNetworkHandler;
import communication.handler.server.SearchResults;
import client.ClientController;
import config.BoardConfiguration;
import config.GameConfiguration;
import server.ClientThread;
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
import model.enums.LandType;
import model.enums.MapChitType;
import model.enums.PathType;
import model.enums.PeerType;
import model.enums.PhaseType;
import model.enums.SearchType;
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
import model.character.belonging.Treasure;
import model.controller.requests.DieRequest;
import model.counter.chit.LostSite;
import model.counter.chit.MapChit;

public class ModelController {

	private ResourceHandler rh;
	private int numPlayers = 0;
	private ArrayList<ValleyChit> sites;
	private int currentDay = 0;
	private LostSite lostCity;
	private LostSite lostCastle;

	private Board board = null;
	private Map<CharacterType, Character> characters;
	private Map<CharacterType, Player> players;
	private Queue<CharacterType> orderOfPlay;
	private boolean currentPlayerDone = false;
	private Set<MapChit> mapChits;
	private Map<MapChitType, ArrayList<Treasure>> treasures;

	private static final RuntimeException noPlayersException = new RuntimeException(
			"There are no players in the queue");

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

		treasures = new HashMap<MapChitType, ArrayList<Treasure>>();

		lostCity = new LostSite(MapChitType.LOST_CITY);
		lostCastle = new LostSite(MapChitType.LOST_CASTLE);
	}

	// TODO belongs in ClientThread.

	public void setCharacterHidden(Player player, boolean hid) {
		player.getCharacter().setHiding(hid);
	}

	// TODO belongs in ClientThread.
	/*
	 * @Override public void peerChoice(PeerType choice, CharacterType actor) {
	 * // choice of // peer if (choice == null) {
	 * System.out.println("PEER CHOICE WAS NULL"); return; } switch (choice) {
	 * case CLUES_AND_PATHS: case CLUES: peerCP(actor); break; default: break; }
	 * 
	 * }
	 */

	// TODO belongs in ClientThread.
	/*
	 * @Override public void hideCharacter(CharacterType actor) {
	 * getClient(actor).rollDie(actor, DieRequest.PEER_TABLE); }
	 */

	// TODO belongs in ClientThread.
	/*
	 * @Override public void setPlayerActivities(List<Activity> activities,
	 * CharacterType chr) { getPlayerOf(chr).setActivities(activities); }
	 */

	// TODO belongs in ClientThread.

	public boolean hideCharacter(int chance, Player player) {
		if (chance <= 5) {
			setCharacterHidden(player, true);
			return true;
		} else {
			setCharacterHidden(player, false);
			return false;
		}
	}

	public boolean moveCharacter(Player ct, TileName tt, int clearing) {
		System.out.println("moving character from: " + board.getLocationOfCounter(ct.getCharacter()
				.getType().toCounter()).getParentTile().getName() + " clearing " + board.getLocationOfCounter(ct.getCharacter()
						.getType().toCounter()).getClearingNumber());
		System.out.println("To: " + tt + " clearing " + clearing);
		
		ClearingInterface cl1 = board.getLocationOfCounter(ct.getCharacter()
				.getType().toCounter());
		ClearingInterface cl2 = board.getClearing(tt, clearing);
		if (cl1.isConnectedTo(cl2, PathType.NORMAL)
				|| ct.hasDiscoveredPath(cl1, cl2)) {
			board.moveCharacter(ct.getCharacter().getType(), tt, clearing);
			
			return true;
		} else {
			return false;
		}
	}

	public void killCharacter(CharacterType character) {
		board.removeCharacter(character);
	}

	public Board setBoard() {
		if (board == null) {
			board = new Board(rh);
			if (!GameConfiguration.Cheat) {
				setUpWarning();
				setUpSoundAndSite();
				setSiteLocations();
			}
		}
		return board;
	}

	/**
	 * called when all the players are ready to start the game. sets up all the
	 * chits and counters visible on the board.
	 */
	public void setBoardForPlay() {
		if (!GameConfiguration.Cheat) {
			setUpSoundAndSite();
			setUpWarning();
			
		}
	}

	/**
	 * sets a player's initial location to the one specified. If none specified,
	 * sets the location to the one specified
	 * 
	 * @param site
	 *            Set to null if not specified.
	 */
	public void setPlayersInitialLocations(CounterType c, CounterType site) {
		// TODO get users to set locations
		if (site != null) {
			board.setLocationOfCounter(c, site);
		} else
			board.setLocationOfCounter(c, GameConfiguration.INITIAL_SITE);
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
		// phases.addAll(getCurrentCharacter().getSpecialPhases());
		phases.addAll(getSunlightPhases());
		return phases;
	}

	public boolean isCharacterHidden(CharacterType ct) {
		return getCharacter(ct).isHiding();
	}

	public Character getCharacter(CharacterType ct) {
		return characters.get(ct);
	}

	/*
	 * private void playActivities(CharacterType chr) { Player plr =
	 * getPlayerOf(chr); for (Activity act :
	 * plr.getPersonalHistory().getCurrentActivities()) { act.perform(this); } }
	 */

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

	public void performSearch(Player player, TableType selectedTable,
			int rollValue) {
		// TODO add other search table options
		switch (selectedTable) {
		default:
			peerTableSearch(player, rollValue);
		}
	}

	private ClientNetworkHandler peerTableSearch(Player player, int roll) {

		TileName ct = getTileOf(player.getCharacter().getType()).getName();
		switch (roll) {
		case 1:
			// TODO player chooses what they want to do in the table.
			roll = 2;
		case 2:
			return peerCP(player);
		case 3:
			return peerHP(player);
		case 4:
			return peerH(player);
		case 5:
			return peerC(player);
		default:
			return new SearchResults(SearchType.NONE);
		}
	}

	private SearchResults peerH(Player player) { // TODO we don't have any enemies yet.
		return new SearchResults(SearchType.NONE);
	}

	private SearchResults peerHP(Player player) { // hidden enemies and paths
		return peerP(player);
	}

	// PEER SEARCH: clues and paths search
	private ClientNetworkHandler peerCP(Player player) {
		SearchResults cResult = peerC(player);
		SearchResults pResults = peerP(player);

		return new SearchResults(SearchType.CLUES_PATHS, cResult.getPeek(),
				pResults.getPaths());
	}

	private SearchResults peerC(Player player) {
		// Player gets to look at the map chits in their tile.
		// they do not discover any sites, but just get to see that they are
		// there.
		// TODO clearing type affects peer
		ArrayList<MapChit> peek = new ArrayList<MapChit>();
		for (MapChit chit : mapChits) {
			if (chit.getTile() == board
					.getLocationOfCounter(
							player.getCharacter().getType().toCounter())
					.getParentTile().getName()) {
				peek.add(chit);
			}
		}
		return new SearchResults(SearchType.CLUES, peek);
	}

	private SearchResults peerP(Player player) { // peer paths
		CounterType playerCounter = player.getCharacter().getType().toCounter();
		ClearingInterface clearing = board.getLocationOfCounter(playerCounter);
		ClearingInterface source = board.getLocationOfCounter(playerCounter);
		Map<ClearingInterface, ClearingInterface> discoveredPaths = new HashMap<ClearingInterface, ClearingInterface>();
		for (ClearingInterface cl : clearing.getSurrounding(PathType.HIDDEN)) {
			player.addDiscoveredPath(source, cl);
			discoveredPaths.put(source, cl);
		}
		return new SearchResults(SearchType.PATHS, discoveredPaths);
	}

	private HexTileInterface getTileOf(CharacterType chr) {
		return board.getLocationOfCounter(chr.toCounter()).getParentTile();
	}

	/*
	 * private TileName getCurrentTile() { return board .getLocationOfCounter(
	 * getCurrentCharacter().getType().toCounter()) .getParentTile().getName();
	 * }
	 */

	public void addTreasure(MapChitType site, TileName tile, int value) {
		board.setLocationOfMapChit(new MapChit(site), tile);
		if (treasures.get(site) == null) {
			treasures.put(site, new ArrayList<Treasure>());
		}
		treasures.get(site).add(new Treasure(value));
		System.out.println("added treasure with value: " + value + " at "
				+ site + " " + tile);
	}

	public void addSound(MapChitType sound, TileName tile, Integer clearing) {
		MapChit mc = new MapChit(sound, clearing);
		mapChits.add(mc);
		board.setLocationOfMapChit(mc, tile);
		System.out.println("added sound chit: " + sound + " " + tile
				+ " with clearing: " + clearing);
	}

	public void addWarning(MapChitType type, TileName tile) {
		MapChit mc = null;
		char t = ' ';
		switch (tile.getType()) {
		case CAVE:
			t = 'C';
			break;
		case MOUNTAIN:
			t = 'M';
			break;
		case VALLEY:
			return;
		case WOODS:
			t = 'W';
			break;
		default:
			break;
		}
		for (MapChit m : mapChits) {
			if (m.getType().type() == ChitType.WARNING) {
				if (m.getIdentifier() == t && m.getType().equals(type)) {
					mc = m;
				}
			}
		}
		board.setLocationOfMapChit(mc, tile);
	}

	public boolean checkIfCave(TileName tile, int clearing) {
		if (board.getClearing(tile, clearing).getLandType() == LandType.CAVE){
			return true;
		}
		return false;
	}
}
