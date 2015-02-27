package model.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.event.RowSorterEvent.Type;

import config.BoardConfiguration;
import config.GameConfiguration;
import controller.Controller;
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
import model.enums.TileType;
import model.enums.ValleyChit;
import model.enums.WarningType;
import model.exceptions.IllegalMoveException;
import model.interfaces.ClearingInterface;
import model.player.Player;
import model.character.CharacterFactory;
import model.character.Character;
import model.character.Phase;
import model.counter.chit.LostSite;
import model.counter.chit.MapChit;

/*
 * Meant to be a container for the entire model
 */
public class ModelController {
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
	private Controller client;

	private static final RuntimeException noPlayersException = new RuntimeException(
			"There are no players in the queue");

	public ModelController(ResourceHandler rh, Controller cln) {
		client = cln;
		this.rh = rh;
		currentDay = 1;
		sites = new ArrayList<ValleyChit>();
		players = new HashMap<CharacterType, Player>();
		numPlayers = GameConfiguration.MAX_PLAYERS;
		orderOfPlay = new LinkedQueue<CharacterType>();
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
	}

	public void raiseMessage(CharacterType plr, String msg) {
		client.displayMessage("Illegal move cancelled.");
	}

	public void moveCharacter(CharacterType characterType, TileName tt,
			int clearing) throws IllegalMoveException {
		Player ct = players.get(characterType);
		ClearingInterface cl1 = board.getLocationOfCounter(characterType
				.toCounter());
		ClearingInterface cl2 = board.getClearing(tt, clearing);
		if (cl1.isConnectedTo(cl2, PathType.NORMAL)
				|| ct.hasDiscoveredPath(cl1, cl2)) {
			board.moveCharacter(characterType, tt, clearing);
			client.moveCounter(characterType.toCounter(), tt, clearing);
		} else {
			throw new IllegalMoveException(tt, clearing, characterType);
		}
	}

	public void killCharacter(Character character) {
		board.removeCharacter(character.getType());
	}

	public Board setBoard() {
		if (board == null) {
			board = new Board(rh);
			setUpWarning();
			setUpSoundAndSite();
		}
		return board;
	}

	public void setNumberPlayers(int maxPlayers) {
		numPlayers = maxPlayers;
	}

	public Collection<Player> getPlayers() {
		return players.values();
	}

	public void setPlayers() {
		if (players != null) {
			List<CharacterType> possible = new ArrayList<CharacterType>();
			randomOrder = new ArrayList<CharacterType>();
			for (CharacterType ct : CharacterType.values()) {
				possible.add(ct);
			}
			for (int i = 0; i < numPlayers; i++) {
				Player plr = new Player(i, "player: " + i);
				CharacterType rnd = Random.remove(possible);
				players.put(rnd, plr);
				plr.setCharacter(characters.get(rnd));
				randomOrder.add(rnd);
			}
		}
	}

	public void setCharacters() {
		if (characters == null) {
			characters = new HashMap<CharacterType, Character>();
			for (Character cr : CharacterFactory.getPossibleCharacters()) {
				characters.put(cr.getType(), cr);
			}
		}
	}

	public Player getCurrentPlayer() {
		try {
			return players.get(orderOfPlay.top());
		} catch (QueueEmptyException e) {
			throw noPlayersException;
		}
	}

	public void setPlayersInitialLocations() {
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

	public void setCurrentPlayerActivities(List<Activity> activities) {
		try {
			players.get(orderOfPlay.top()).setActivities(activities);
		} catch (QueueEmptyException e) {
			throw noPlayersException;
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

	public Character getCurrentCharacter() {
		return getCurrentPlayer().getCharacter();
	}

	public CharacterType getCurrentCharacterType() {
		return getCurrentCharacter().getType();
	}

	public boolean isPlayerDone() {
		return currentPlayerDone;
	}

	synchronized public void setPlayerDone() {
		currentPlayerDone = true;
		this.notify();
	}

	public Player nextPlayer() {
		if (orderOfPlay.isEmpty()) {
			resetOrderOfPlay();
		}
		currentPlayerDone = false;
		client.setCurrentCharacter(getCurrentCharacterType());
		try {
			return players.get(orderOfPlay.pop());
		} catch (QueueEmptyException e) {
			throw noPlayersException;
		}
	}

	public Board getBoard() {
		return board;
	}

	public void newDayTime() {
		resetOrderOfPlay();
	}

	public void newDay() {
		for (Player player : players.values()) {
			player.getPersonalHistory().newDay();
		}

	}

	public ArrayList<Phase> getAllowedPhases() {
		ArrayList<Phase> phases = new ArrayList<Phase>();
		phases.addAll(getInitialPhases());
		phases.addAll(getCurrentCharacter().getSpecialPhases());
		phases.addAll(getSunlightPhases());
		return phases;
	}

	public boolean isCharacterHidden(CharacterType ct) {
		return getCharacter(ct).isHiding();
	}

	public Character getCharacter(CharacterType ct) {
		return characters.get(ct);
	}

	public boolean isCurrentHidden() {
		return getCurrentCharacter().isHiding();
	}

	public void setCurrentHiding() {
		getCurrentCharacter().setHiding(true);
		client.setHiding(getCurrentCharacterType());
		System.out.println("current is now hiding.");
	}

	public void unhideCurrent() {
		getCurrentCharacter().setHiding(false);
	}

	public boolean isCharacterHiding(CharacterType actor) {
		return characters.get(actor).isHiding();
	}

	public void startSearching(CharacterType actor) {
		client.startSearch(actor);
	}

	private void resetOrderOfPlay() {
		Random.shuffle(randomOrder);
		orderOfPlay.clear();
		for (CharacterType ct : randomOrder) {
			orderOfPlay.push(ct);
		}
		System.out.println(orderOfPlay);
	}

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
		for(TileName tn: new TileName[] {
				TileName.CAVERN,
				TileName.CAVES,
				TileName.HIGH_PASS,
				TileName.BORDERLAND,
				TileName.RUINS
		}) {
			board.setLocationOfMapChit(Random.remove(cave), tn);
		}
		
		for(TileName tn: new TileName[] {
				TileName.LINDEN_WOODS,
				TileName.MAPLE_WOODS,
				TileName.NUT_WOODS,
				TileName.OAK_WOODS,
				TileName.PINE_WOODS
		}) {
			board.setLocationOfMapChit(Random.remove(woods), tn);
		}
		
		for(TileName tn: new TileName[] {
				TileName.CLIFF,
				TileName.CRAG,
				TileName.DEEP_WOODS,
				TileName.LEDGES,
				TileName.MOUNTAIN
		}) {
			board.setLocationOfMapChit(Random.remove(mountain), tn);
		}
	}
	
	private static List<Integer> makeClearings() {
		ArrayList<Integer> ret = new ArrayList<Integer>();
		for(int i = 1; i <= BoardConfiguration.MAX_CLEARINGS_IN_TILE; ++i) {
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
		for(MapChitType chit: MapChitType.SOUNDS) {
			List<Integer> clears = makeClearings();
			MapChit mc;
			for(int i = 0; i < 2; ++i) {
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

		for(int i = 0; i < 4; ++i) {
			cityList.add(Random.remove(chits));
			castleList.add(Random.remove(chits));
		}

		// add corresponding chits.
		cityList.add(lostCity);

		castleList.add(lostCastle);

		// each chit in city list, goes on a cave tile.
		for(TileName tn: new TileName[] {
				TileName.BORDERLAND,
				TileName.CAVERN,
				TileName.CAVES,
				TileName.HIGH_PASS,
				TileName.RUINS
		}) {
			board.setLocationOfMapChit(Random.remove(cityList), tn);
		}

		// each chit in castle list goes on mountain tile
		for(TileName tn: new TileName[] {
				TileName.CLIFF,
				TileName.CRAG,
				TileName.DEEP_WOODS,
				TileName.LEDGES,
				TileName.MOUNTAIN
		}) {
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

	public void performSearch(TableType selectedTable) {
		switch (selectedTable) {
		default:
			// peer table.
			peerTableSearch();
		}

	}

	private void peerTableSearch() {
		int roll = 2;// Random.dieRoll(); TODO cheat mode
		TileName ct = getCurrentTile();
		switch (roll) {
		case 1:
			peerChoice();
			break;
		case 2:
			peerCP();
			showMessage("Found hidden paths and clues in " + ct);
			break;
		case 3:
			peerHP();
			showMessage("Found hidden paths in " + ct);
			break;
		case 4:
			peerH();
			break;
		case 5:
			peerC();
			showMessage("Found hidden clues in " + ct);
			break;
		default:
			showMessage("Peer has failed");
			break;
		}
	}

	private void showMessage(String string) {
		CharacterType ct = getCurrentCharacter().getType();
		client.displayMessage(ct + "\n" + string);
	}

	private void peerC() {
		// Player gets to look at the map chits in their tile. 
		// they do not discover any sites, but just get to see that they are there.
		ArrayList<MapChit> peek = new ArrayList<MapChit>();
		for(MapChit chit : mapChits){
			if(chit.getTile() == board.getLocationOfCounter(getCurrentCharacter().getType().toCounter()).getParentTile().getName()){
				peek.add(chit);
			}
		}
		getCurrentPlayer().discoverAllMapChits(peek);
		client.revealMapChits(peek);
	}

	private void peerH() {
		// we don't have any enemies yet.

	}

	private void peerHP() { // hidden enemies and paths
		// TODO Auto-generated method stub
		peerP();
	}

	// PEER SEARCH: clues and paths search
	private void peerCP() {
		peerC();
		peerP();
	}

	private void peerP() { // peer paths
		ClearingInterface clearing = board
				.getLocationOfCounter(getCurrentPlayer().getCharacter()
						.getType().toCounter());
		ClearingInterface source = board
				.getLocationOfCounter(getCurrentPlayer().getCharacter()
						.getType().toCounter());
		for (ClearingInterface cl : clearing.getSurrounding(PathType.HIDDEN)) {
			getCurrentPlayer().addDiscoveredPath(source, cl);
		}
	}

	private void peerChoice() { // choice of peer
		PeerType choice = client.getPeerChoice();
		if (choice == null) {
			System.out.println("PEER CHOICE WAS NULL");
			return;
		}
		switch (choice) {
		case CLUES_AND_PATHS:
		case CLUES:
			peerCP();
			break;
		default:
			break;
		}

	}

	private TileName getCurrentTile() {
		return board
				.getLocationOfCounter(
						getCurrentCharacter().getType().toCounter())
				.getParentTile().getName();
	}

}
