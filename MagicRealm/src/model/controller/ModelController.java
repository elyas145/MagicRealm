package model.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import model.enums.PhaseType;
import model.enums.SiteType;
import model.enums.TileName;
import model.exceptions.IllegalMoveException;
import model.player.Player;
import model.character.CharacterFactory;
import model.character.Character;
import model.character.Phase;

/*
 * Meant to be a container for the entire model
 */
public class ModelController {
	private ResourceHandler rh;
	private int numPlayers = 0;
	private ArrayList<SiteType> sites;
	private int currentDay = 0;

	private Board board = null;
	//private ArrayList<Character> characters = null;
	private Map<CharacterType, Character> characters;
	//private ArrayList<Player> players = null;
	private Map<CharacterType, Player> players;
	private List<CharacterType> randomOrder;
	private Queue<CharacterType> orderOfPlay;
	private boolean currentPlayerDone = false;
	
	private Controller client;
	
	private static final RuntimeException noPlayersException = new RuntimeException("There are no players in the queue");

	public ModelController(ResourceHandler rh, Controller cln) {
		client = cln;
		this.rh = rh;
		currentDay = 1;
		sites = new ArrayList<SiteType>();
		players = new HashMap<CharacterType, Player>();
		numPlayers = GameConfiguration.MAX_PLAYERS;
		orderOfPlay = new LinkedQueue<CharacterType>();
		for (SiteType t : SiteType.values()) {
			sites.add(t);
		}
	}
	
	public void raiseMessage(CharacterType plr, String msg) {
		client.displayMessage("Illegal move cancelled.");
	}

	public void moveCharacter(CharacterType characterType, TileName tt, int clearing)
			throws IllegalMoveException {
		if(board.isValidMove(characterType.toCounter(), tt, clearing)) {
			board.moveCharacter(characterType, tt, clearing);
			client.moveCounter(characterType.toCounter(), tt, clearing);
		}
		else {
			throw new IllegalMoveException(tt, clearing, characterType);
		}
	}

	public void killCharacter(Character character) {
		board.removeCharacter(character.getType());
	}

	public Board setBoard() {
		if (board == null) {
			board = new Board(rh);
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
			for(CharacterType ct: CharacterType.values()) {
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
			for(Character cr: CharacterFactory.getPossibleCharacters()) {
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
			board.setLocationOfCounter(c.getType().toCounter(), BoardConfiguration.INITIAL_SITE);
		}

	}

	public void setSiteLocations() {
		for (SiteType t : sites) {
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

	public void setCurrentPlayerActivities(ArrayList<Activity> activities) {
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
		if(orderOfPlay.isEmpty()) {
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
	
	private void resetOrderOfPlay() {
		Random.shuffle(randomOrder);
		orderOfPlay.clear();
		for(CharacterType ct: randomOrder) {
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

}
