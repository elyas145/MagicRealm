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

	void onSplashScreenEnd();

	void exit();

	void startGameView();

	void setPlayerActivities(CharacterType player, List<Activity> activities);

	void startGame();

	void displayMessage(String string);

	void setHiding(CharacterType character, boolean hid);

	void setCurrentCharacter(CharacterType character);

	void moveCounter(CounterType counter, TileName tt, int clearing);

	void startSearch(CharacterType actor);

	void focusOnBoard(TileName tile);

	void focusOnBoard(TileName tile, int clearing);

	void focusOnBoard(CounterType counter);

	void revealMapChits(Iterable<MapChit> chits);

	void raiseException(MRException exception);

	void performPeerChoice();

	void rollDie(CharacterType actor, DieRequest peerTable);

	void initializeBoard(NetworkHandler<BoardView> initializer);

	BoardView startBoardView();

	void startBirdsong();

	void enterLobby();

}
