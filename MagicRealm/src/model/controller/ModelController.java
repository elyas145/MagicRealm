package model.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import model.enums.MapChitType;
import model.enums.PhaseType;
import model.enums.TileName;
import model.enums.ValleyChit;
import model.enums.WarningType;
import model.exceptions.IllegalMoveException;
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
	private ArrayList<MapChit> mapChits;
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

		mapChits = new ArrayList<MapChit>();
		for (MapChitType type : MapChitType.values()) {
			if(type.type() == ChitType.WARNING){
				mapChits.add(new MapChit(type, 'C'));
				mapChits.add(new MapChit(type, 'W'));
				mapChits.add(new MapChit(type, 'M'));
			}			
		}

		lostCity = new LostSite(MapChitType.LOST_CITY);
		lostCastle = new LostSite(MapChitType.LOST_CASTLE);
	}

	public void raiseMessage(CharacterType plr, String msg) {
		client.displayMessage("Illegal move cancelled.");
	}

	public void moveCharacter(CharacterType characterType, TileName tt,
			int clearing) throws IllegalMoveException {
		if (board.isValidMove(characterType.toCounter(), tt, clearing)) {
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
		// TODO Auto-generated method stub
		return null;
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
			if(chit.getType().type() == ChitType.WARNING){
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
		Random.shuffle(cave);
		// set the tiles
		cave.get(0).setTile(TileName.CAVERN);
		cave.get(1).setTile(TileName.CAVES);
		cave.get(2).setTile(TileName.HIGH_PASS);
		cave.get(3).setTile(TileName.BORDERLAND);
		cave.get(4).setTile(TileName.RUINS);

		woods.get(0).setTile(TileName.LINDEN_WOODS);
		woods.get(1).setTile(TileName.MAPLE_WOODS);
		woods.get(2).setTile(TileName.NUT_WOODS);
		woods.get(3).setTile(TileName.OAK_WOODS);
		woods.get(4).setTile(TileName.PINE_WOODS);

		mountain.get(0).setTile(TileName.CLIFF);
		mountain.get(1).setTile(TileName.CRAG);
		mountain.get(2).setTile(TileName.DEEP_WOODS);
		mountain.get(3).setTile(TileName.LEDGES);
		mountain.get(4).setTile(TileName.MOUNTAIN);
	}

	private void setUpSoundAndSite() {
		ArrayList<MapChit> chits = new ArrayList<MapChit>();

		// add sound and site chits to array.
		for (MapChitType chit : MapChitType.values()) {
			if (chit.type() == ChitType.SOUND || chit.type() == ChitType.SITE) {
				chits.add(new MapChit(chit));
			}
		}
		Random.shuffle(chits);
		ArrayList<MapChit> lostCityChits = new ArrayList<MapChit>();
		ArrayList<MapChit> lostCastleChits = new ArrayList<MapChit>();

		for (int i = 0; i < 5; i++) {
			// add 5 chits to lost city.
			lostCityChits.add(chits.get(0));
			chits.remove(0);
			// add 5 chits to lost castle.
			lostCastleChits.add(chits.get(0));
			chits.remove(0);
		}
		lostCastle.setWarningAndSite(lostCastleChits);
		lostCity.setWarningAndSite(lostCityChits);

		// rest of chits to be split up into two groups of 4.
		// one group gets the lost castle chit, and the other gets the lost
		// city.
		ArrayList<MapChit> cityList = new ArrayList<MapChit>();
		ArrayList<MapChit> castleList = new ArrayList<MapChit>();

		for (int i = 0; i < 4; i++) {
			cityList.add(chits.get(0));
			chits.remove(0);
			castleList.add(chits.get(0));
			chits.remove(0);
		}

		// add corresponding chits.
		cityList.add(lostCity);
		Random.shuffle(cityList);

		castleList.add(lostCastle);
		Random.shuffle(castleList);

		// each chit in city list, goes on a cave tile.
		board.setLocationOfMapChit(cityList.get(0).getType(),
				TileName.BORDERLAND);
		cityList.get(0).setTile(TileName.BORDERLAND);
		board.setLocationOfMapChit(cityList.get(1).getType(), TileName.CAVERN);
		cityList.get(1).setTile(TileName.CAVERN);
		board.setLocationOfMapChit(cityList.get(2).getType(), TileName.CAVES);
		cityList.get(2).setTile(TileName.CAVES);
		board.setLocationOfMapChit(cityList.get(3).getType(),
				TileName.HIGH_PASS);
		cityList.get(3).setTile(TileName.HIGH_PASS);
		board.setLocationOfMapChit(cityList.get(4).getType(), TileName.RUINS);
		cityList.get(4).setTile(TileName.RUINS);
		
		// each chit in castle list goes on mountain tile
		board.setLocationOfMapChit(castleList.get(0).getType(), TileName.CLIFF);
		castleList.get(0).setTile(TileName.CLIFF);
		board.setLocationOfMapChit(castleList.get(1).getType(), TileName.CRAG);
		castleList.get(1).setTile(TileName.CRAG);
		board.setLocationOfMapChit(castleList.get(2).getType(),
				TileName.DEEP_WOODS);
		castleList.get(2).setTile(TileName.DEEP_WOODS);
		board.setLocationOfMapChit(castleList.get(3).getType(), TileName.LEDGES);
		castleList.get(3).setTile(TileName.LEDGES);
		board.setLocationOfMapChit(castleList.get(4).getType(),
				TileName.MOUNTAIN);
		castleList.get(4).setTile(TileName.MOUNTAIN);
		
		//add all chits to one array list
		
		
	}

	public ArrayList<MapChit> getMapChits() {
		return mapChits;
	}

}
