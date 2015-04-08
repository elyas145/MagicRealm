package model.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import communication.handler.server.SearchResults;
import communication.handler.server.serialized.SerializedMapChit;
import config.BoardConfiguration;
import config.GameConfiguration;
import server.ClientThread;
import utils.random.Random;
import utils.resources.ResourceHandler;
import utils.structures.Queue;
import utils.structures.QueueEmptyException;
import model.activity.Activity;
import model.board.Board;
import model.board.tile.HexTile;
import model.enums.ActivityType;
import model.enums.CharacterType;
import model.enums.ChitType;
import model.enums.CounterType;
import model.enums.LandType;
import model.enums.MapChitType;
import model.enums.PathType;
import model.enums.PhaseType;
import model.enums.SearchType;
import model.enums.TableType;
import model.enums.TileName;
import model.enums.ValleyChit;
import model.interfaces.ClearingInterface;
import model.player.Player;
import model.character.Character;
import model.character.Phase;
import model.character.belonging.Treasure;
import model.counter.chit.LostSite;
import model.counter.chit.MapChit;

public class ModelController {

	private ResourceHandler rh;
	private int numPlayers = 0;
	private ArrayList<ValleyChit> dwellings;
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
	private boolean discoveredCastle = false;
	private boolean discoveredCity = false;
	private static final RuntimeException noPlayersException = new RuntimeException(
			"There are no players in the queue");

	public ModelController(ResourceHandler rh) {
		this.rh = rh;
		currentDay = 1;
		dwellings = new ArrayList<ValleyChit>();

		for (ValleyChit t : ValleyChit.values()) {
			dwellings.add(t);
		}

		mapChits = new HashSet<MapChit>();

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
		System.out.println("moving character from: "
				+ board.getLocationOfCounter(
						ct.getCharacter().getType().toCounter())
						.getParentTile().getName()
				+ " clearing "
				+ board.getLocationOfCounter(
						ct.getCharacter().getType().toCounter())
						.getClearingNumber());
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
			setDwellingLocations();
		}
		return board;
	}

	/**
	 * called when all the players are ready to start the game. sets up all the
	 * chits and counters visible on the board.
	 */
	public void setBoardForPlay() {
		if (GameConfiguration.RANDOM || !GameConfiguration.Cheat) {
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

	public void setDwellingLocations() {
		for (ValleyChit t : dwellings) {
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
		for (MapChitType type : MapChitType.WARNINGS) {
			mapChits.add(new MapChit(type, 'C'));
			mapChits.add(new MapChit(type, 'W'));
			mapChits.add(new MapChit(type, 'M'));
		}
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
		int loc = 0;
		// add sound and site chits to array.
		for (MapChitType chit : MapChitType.SITES) {
			switch (chit) {
			case STATUE:
				loc = 2;
				break;
			case ALTAR:
				loc = 1;
				break;
			case VAULT:
				loc = 3;
				break;
			case POOL:
				loc = 6;
				break;
			case HOARD:
				loc = 6;
				break;
			case LAIR:
				loc = 3;
				break;
			case CAIRNS:
				loc = 5;
				break;
			case SHRINE:
				loc = 4;
				break;
			default:
				break;
			}
			MapChit mc = new MapChit(chit, loc);
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
			MapChit c = Random.remove(cityList);
			c.setTile(tn);
			board.setLocationOfMapChit(c, tn);
		}

		// each chit in castle list goes on mountain tile
		for (TileName tn : new TileName[] { TileName.CLIFF, TileName.CRAG,
				TileName.DEEP_WOODS, TileName.LEDGES, TileName.MOUNTAIN }) {
			MapChit c = Random.remove(castleList);
			c.setTile(tn);
			board.setLocationOfMapChit(c, tn);
		}

		ArrayList<MapChit> lostCityChits = new ArrayList<MapChit>();
		ArrayList<MapChit> lostCastleChits = new ArrayList<MapChit>();
		for (int i = 0; i < 5; i++) {
			// add 5 chits to lost city.
			MapChit c = Random.remove(chits);
			c.setTile(lostCity.getTile());
			lostCityChits.add(c);
			// add 5 chits to lost castle.
			MapChit d = Random.remove(chits);
			d.setTile(lostCastle.getTile());
			lostCastleChits.add(d);
		}
		lostCastle.setWarningAndSite(lostCastleChits);
		lostCity.setWarningAndSite(lostCityChits);
		mapChits.add(lostCastle);
		mapChits.add(lostCity);
		board.addChitsToLoad(lostCastle.getWarningAndSite());
		board.addChitsToLoad(lostCity.getWarningAndSite());
	}

	public Iterable<MapChit> getMapChits() {
		return mapChits;
	}

	public SearchResults performSearch(Player player, TableType selectedTable,
			int rollValue) {
		// TODO add other search table options
		switch (selectedTable) {
		case PEER:
			return peerTableSearch(player, rollValue);
		case LOCATE:
			return locateTableSearch(player, rollValue);
		case LOOT:
			return lootSearch(player, rollValue);
		default:
			return null;
		}
	}

	private SearchResults lootSearch(Player player, int rollValue) {
		TileName myTile = board
				.getLocationOfCounter(
						player.getCharacter().getType().toCounter())
				.getParentTile().getName();
		int myClearing = board.getLocationOfCounter(
				player.getCharacter().getType().toCounter())
				.getClearingNumber();
		for (MapChit c : board.getMapChitLocations().keySet()) {
			if ((c.getTile() == myTile) && (c.getClearing() == myClearing)
					&& (c.getType().type() == ChitType.SITE)
					&& (player.hasDiscoveredSite(c))) {
				return new SearchResults(SearchType.LOOT,
						GameConfiguration.MAX_TREASURE_VALUE / rollValue,
						c.getType());
			}
		}
		return new SearchResults(SearchType.NONE);
	}

	private SearchResults locateTableSearch(Player player, int rollValue) {
		switch (rollValue) {
		case 2: // passages and clues
			SearchResults p = searchPassages(player);
			SearchResults c = peerC(player);
			return new SearchResults(SearchType.PASSAGES_CLUES, c.getPeek(),
					p.getPaths());
		case 3: // passages
			return searchPassages(player);
		case 4:
			return discoverChits(player);
		}
		return new SearchResults(SearchType.NONE);
	}

	private SearchResults discoverChits(Player player) {
		ArrayList<MapChit> peek = new ArrayList<MapChit>();
		boolean castle = false;
		boolean city = false;
		LandType land = board.getLocationOfCounter(player.getCharacter().getType().toCounter()).getLandType();
		for (MapChit chit : mapChits) {
			System.out.println("Current map chit: " + chit);
			boolean tile = chit.getTile() == board
					.getLocationOfCounter(
							player.getCharacter().getType().toCounter())
					.getParentTile().getName();
			boolean clearing = chit.getClearing() == board.getLocationOfCounter(
					player.getCharacter().getType().toCounter())
					.getClearingNumber();
			boolean toCheck = land == LandType.MOUNTAIN ? tile : tile && clearing;
			
			if (toCheck) {
				System.out.println("this chit is on my clearing.");
				if (chit.getType() == MapChitType.LOST_CITY) {
					System.out.println("this chit is lost city");
					city = true;
				} else if (chit.getType() == MapChitType.LOST_CASTLE) {
					System.out.println("this chit is lost castle");
					castle = true;
				}
				if (lostCity.getWarningAndSite().contains(chit)) {
					System.out.println("this chit is inside lost city.");
					if (discoveredCity) {
						System.out.println("lost city discovered");
						peek.add(chit);
					} else {
						city = true;
					}
				} else if (lostCastle.getWarningAndSite().contains(chit)) {
					System.out.println("this chit is inside lost castle.");
					if (discoveredCastle) {
						System.out.println("lost castle discovered");
						peek.add(chit);
					} else {
						castle = true;
					}
				} else {
					System.out
							.println("this chit has nothing to do with lost city nd lost castle.");
					peek.add(chit);
				}
			}
		}
		player.discoverAllMapChits(peek);
		if (castle)
			discoveredCastle = true;
		if (city)
			discoveredCity = true;
		return new SearchResults(SearchType.DISCOVER_CHITS, peek, null, castle,
				city);
	}

	private SearchResults searchPassages(Player player) {
		CounterType playerCounter = player.getCharacter().getType().toCounter();
		ClearingInterface source = board.getLocationOfCounter(playerCounter);
		Map<ClearingInterface, ClearingInterface> discoveredPaths = new HashMap<ClearingInterface, ClearingInterface>();
		ArrayList<String> str = new ArrayList<String>();
		for (ClearingInterface cl : source.getSurrounding(PathType.SECRET)) {
			if (!player.hasDiscoveredPath(cl, source)) {
				player.addDiscoveredPath(cl, source);
				discoveredPaths.put(cl, source);
				str.add(cl.getParentTile().getName().toString() + ": "
						+ source.getClearingNumber() + ", "
						+ cl.getClearingNumber());
			}
		}
		return new SearchResults(SearchType.PASSAGES, null, str);
	}

	private SearchResults peerTableSearch(Player player, int roll) {
		switch (roll) {
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

	private SearchResults peerH(Player player) { // TODO we don't have any
													// enemies yet.
		return new SearchResults(SearchType.NONE);
	}

	private SearchResults peerHP(Player player) { // hidden enemies and paths
		return peerP(player);
	}

	// PEER SEARCH: clues and paths search
	private SearchResults peerCP(Player player) {
		SearchResults cResult = peerC(player);
		SearchResults pResults = peerP(player);
		SearchResults res = new SearchResults(SearchType.CLUES_PATHS);
		if (cResult.isCastle()) {
			res.setCastle(true);
		}
		if (cResult.isCity()) {
			res.setCity(true);
		}
		res.setPeek(cResult.getPeek());
		res.setPaths(pResults.getPaths());
		return res;
	}

	private SearchResults peerC(Player player) {
		// Player gets to look at the map chits in their tile.
		// they do not discover any sites, but just get to see that they are
		// there.
		// TODO clearing type affects peer (mountain can see adjacent tiles)
		ArrayList<MapChit> peek = new ArrayList<MapChit>();
		boolean city = false;
		boolean castle = false;
		ArrayList<TileName> surrounding;
		CounterType counter = player.getCharacter().getType().toCounter();
		TileName curr = board
				.getLocationOfCounter(
						player.getCharacter().getType().toCounter())
				.getParentTile().getName();
		if (board.getLocationOfCounter(counter).getLandType() == LandType.MOUNTAIN) {
			surrounding = new ArrayList<TileName>(board.getTile(curr)
					.getSurrounding());
		} else {
			surrounding = new ArrayList<TileName>();
		}
		surrounding.add(curr);
		boolean discoverable;
		for (MapChit chit : mapChits) {
			discoverable = false;
			for (TileName tn : surrounding) {
				if (chit.getTile() == tn) {
					discoverable = true;
					break;
				}
			}
			if (discoverable) {
				if (chit.getType() == MapChitType.LOST_CASTLE) {
					if (!discoveredCastle)
						castle = true;
				} else if (chit.getType() == MapChitType.LOST_CITY) {
					if (!discoveredCity)
						city = true;
				}
				if (lostCity.getWarningAndSite().contains(chit)) {
					if (discoveredCity) {
						peek.add(chit);
					}
				} else if (lostCastle.getWarningAndSite().contains(chit)) {
					if (discoveredCastle) {
						peek.add(chit);
					}
				} else {
					peek.add(chit);
				}
			}
		}

		return new SearchResults(SearchType.CLUES, peek, null, castle, city);
	}

	private SearchResults peerP(Player player) { // peer paths
		CounterType playerCounter = player.getCharacter().getType().toCounter();
		ClearingInterface clearing = board.getLocationOfCounter(playerCounter);
		ClearingInterface source = board.getLocationOfCounter(playerCounter);
		Map<ClearingInterface, ClearingInterface> discoveredPaths = new HashMap<ClearingInterface, ClearingInterface>();
		ArrayList<String> str = new ArrayList<String>();
		for (ClearingInterface cl : clearing.getSurrounding(PathType.HIDDEN)) {
			if (!player.hasDiscoveredPath(cl, source)) {
				player.addDiscoveredPath(cl, source);
				discoveredPaths.put(cl, source);
				str.add(cl.getParentTile().getName().toString() + ": "
						+ source.getClearingNumber() + ", "
						+ cl.getClearingNumber());
			}
		}

		return new SearchResults(SearchType.PATHS, null, str);
	}

	/*
	 * private TileName getCurrentTile() { return board .getLocationOfCounter(
	 * getCurrentCharacter().getType().toCounter()) .getParentTile().getName();
	 * }
	 */

	public void addSite(MapChitType site, TileName tile) {
		board.setLocationOfMapChit(new MapChit(site), tile);
		mapChits.add(new MapChit(site, site.getClearing(), tile));
		System.out.println("added site at " + tile);
	}

	public void addSound(MapChitType sound, TileName tile) {
		MapChit mc = new MapChit(sound, sound.getClearing());
		mapChits.add(mc);
		board.setLocationOfMapChit(mc, tile);
		System.out.println("added sound chit: " + sound + " " + tile);
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
		if (board.getClearing(tile, clearing).getLandType() == LandType.CAVE) {
			return true;
		}
		return false;
	}

	public LostSite getCastle() {
		return lostCastle;
	}

	public LostSite getCity() {
		return lostCity;
	}

	public void setLostCastleFound(boolean b) {
		discoveredCastle = b;
	}

	public void setLostCityFound(boolean b) {
		discoveredCity = b;
	}

	public ArrayList<SerializedMapChit> getLostChits(MapChitType type) {
		ArrayList<SerializedMapChit> smapchits = new ArrayList<SerializedMapChit>();
		switch (type) {
		case LOST_CITY:
			for (MapChit c : lostCity.getWarningAndSite()) {
				smapchits.add(c.getSerializedChit());
			}
			break;
		case LOST_CASTLE:
			for (MapChit c : lostCastle.getWarningAndSite()) {
				smapchits.add(c.getSerializedChit());
			}
			break;
		default:
			break;
		}
		return smapchits;
	}

	public void setLost(MapChitType site, ArrayList<MapChitType> array,
			TileName tile) {
		System.out.println("set " + site);
		ArrayList<MapChit> mcArray = new ArrayList<MapChit>();
		for (MapChitType mc : array) {
			MapChit c = new MapChit(tile, mc);
			mcArray.add(c);
			mapChits.add(c);
		}
		switch (site) {
		case LOST_CASTLE:
			lostCastle = new LostSite(MapChitType.LOST_CASTLE);
			lostCastle.setTile(tile);
			lostCastle.setWarningAndSite(mcArray);
			lostCastle.setClearing(MapChitType.LOST_CASTLE.getClearing());
			board.setLocationOfMapChit(lostCastle, tile);
			board.addChitsToLoad(lostCastle.getWarningAndSite());
			mapChits.add(lostCastle);
			break;
		case LOST_CITY:
			lostCity = new LostSite(MapChitType.LOST_CITY);
			lostCity.setTile(tile);
			lostCity.setWarningAndSite(mcArray);
			lostCity.setClearing(MapChitType.LOST_CITY.getClearing());
			board.setLocationOfMapChit(lostCity, tile);
			mapChits.add(lostCity);
			board.addChitsToLoad(lostCity.getWarningAndSite());
			break;
		default:
			break;
		}
	}

	public boolean enchantTile(Player player) {
		return board.setEnchantedTile(board
				.getLocationOfCounter(
						player.getCharacter().getType().toCounter())
				.getParentTile().getName());
	}

	public TileName getLocation(Character character) {
		return board.getLocationOfCounter(character.getType().toCounter())
				.getParentTile().getName();
	}
}
