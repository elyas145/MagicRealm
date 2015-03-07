package controller;

import java.util.List;

import view.controller.game.BoardView;
import network.NetworkHandler;
import model.activity.Activity;
import model.board.Board;
import model.controller.requests.DieRequest;
import model.counter.chit.MapChit;
import model.enums.CharacterType;
import model.enums.CounterType;
import model.enums.TileName;
import model.exceptions.MRException;
import model.interfaces.HexTileInterface;

public interface ClientController {

	void displayMessage(String string);

	void enterBirdSong();

	void enterCharacterSelection();

	void enterLobby(int currentPlayers);

	void exit();

	void focusOnBoard(CounterType counter);

	void focusOnBoard(TileName tile);

	void focusOnBoard(TileName tile, int clearing);

	void focusOnCharacter(CharacterType character);

	void initializeBoard(NetworkHandler<BoardView> initializer);

	void moveCounter(CounterType counter, TileName tt, int clearing);

	void performPeerChoice();

	void raiseException(MRException exception);

	void revealMapChits(Iterable<MapChit> chits);

	void rollDie(CharacterType actor, DieRequest peerTable);

	void setHiding(CharacterType character, boolean hid);

	void setPlayerActivities(CharacterType player, List<Activity> activities);

	BoardView startBoardView();

	void startGame();

	void startGameView();

	void startSearch(CharacterType actor);

}
