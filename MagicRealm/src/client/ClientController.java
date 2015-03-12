package client;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

import communication.ClientNetworkHandler;
import communication.handler.server.serialized.SerializedBoard;
import view.controller.game.BoardView;
import lwjglview.graphics.board.LWJGLBoardDrawable;
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

	void enterLobby(SerializedBoard sboard);

	void exit();

	void focusOnBoard(CounterType counter);

	void focusOnBoard(TileName tile);

	void focusOnBoard(TileName tile, int clearing);

	void focusOnCharacter(CharacterType character);

	void moveCounter(CounterType counter, TileName tt, int clearing);

	void performPeerChoice();

	void raiseException(MRException exception);

	void revealMapChits(Iterable<MapChit> chits);

	void rollDie(CharacterType actor, DieRequest peerTable);

	void setHiding(CharacterType character, boolean hid);

	void setPlayerActivities(CharacterType player, List<Activity> activities);

	BoardView startBoardView();

	void startSearch(CharacterType actor);

	void setID(int id);

	void connect(String ipaddress, int port) throws UnknownHostException, IOException;

	void initializeBoard(SerializedBoard board);

	void updateLobbyCount(int count);

	void startGame(SerializedBoard board);

	void updateCharacterSelection(CharacterType character);

	void setBoardView(LWJGLBoardDrawable board);

}
