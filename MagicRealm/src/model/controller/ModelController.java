package model.controller;

import java.util.ArrayList;
import java.util.Map;

import config.GameConfiguration;
import utils.resources.ResourceHandler;
import model.activity.Activity;
import model.board.Board;
import model.enums.CharacterType;
import model.enums.CounterType;
import model.enums.SiteType;
import model.enums.TileName;
import model.exceptions.IllegalMoveException;
import model.player.Player;
import model.character.CharacterFactory;
import model.character.Character;

/*
 * Meant to be a container for the entire model
 */
public class ModelController {
	private ResourceHandler rh;
	private int numPlayers = 0;
	private ArrayList<SiteType> sites;
	private int currentDay = 0;

	private Board board = null;
	private ArrayList<Character> characters = null;
	private ArrayList<Player> players = null;
	private int currentPlayer = 0;
	private boolean currentPlayerDone = false;

	public ModelController(ResourceHandler rh) {
		this.rh = rh;
		currentDay = 1;
		sites = new ArrayList<SiteType>();
		players = new ArrayList<Player>();
		numPlayers = GameConfiguration.MAX_PLAYERS;
		for (SiteType t : SiteType.values()) {
			sites.add(t);
		}
	}

	public void moveCharacter(Character character, TileName tt, int clearing)
			throws IllegalMoveException {
		board.moveCharacter(character, tt, clearing);
	}

	public void killCharacter(Character character) {
		board.removeCharacter(character);
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

	public ArrayList<Player> getPlayers() {
		return players;
	}

	public void setPlayers() {
		if (players != null) {
			for (int i = 0; i < numPlayers; i++) {
				players.add(new Player(i, "player: " + i));
				players.get(i).setCharacter(characters.get(i));
			}
		}
	}

	public void setCharacters() {
		if (characters == null) {
			characters = CharacterFactory.getPossibleCharacters();
		}
	}

	public Player getCurrentPlayer() {
		return players.get(currentPlayer);
	}

	public void setPlayersInitialLocations() {
		for (Character c : characters) {
			board.setLocationOfCounter(c.getType().toCounter(), SiteType.INN);
		}

	}

	public void setSiteLocations() {
		for (SiteType t : sites) {
			switch (t) {
			case CHAPEL:
				board.setLocationOfCounter(t.toCounterType(),
						TileName.AWFUL_VALLEY, 5);
			case GUARD_HOUSE:
				board.setLocationOfCounter(t.toCounterType(),
						TileName.DARK_VALLEY, 5);
			case HOUSE:
				board.setLocationOfCounter(t.toCounterType(),
						TileName.CURST_VALLEY, 5);
			case INN:
				board.setLocationOfCounter(t.toCounterType(),
						TileName.BAD_VALLEY, 5);
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
		players.get(currentPlayer).setActivities(activities);
	}

	public ArrayList<Activity> getCurrentActivities() {
		return players.get(currentPlayer).getPersonalHistory()
				.getCurrentActivities();
	}

	public CounterType getCurrentCounter() {
		return characters.get(currentPlayer).getType().toCounter();
	}

	public boolean isPlayerDone() {
		return currentPlayerDone;
	}

	synchronized public void setPlayerDone() {
		currentPlayerDone = true;
	}

	public Player nextPlayer() {
		if (currentPlayer < 2) {
			currentPlayer++;
		} else
			currentPlayer = 0;
		return players.get(currentPlayer);
	}

	public Board getBoard() {
		return board;
	}
}
