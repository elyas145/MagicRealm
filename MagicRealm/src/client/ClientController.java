package client;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import communication.ClientNetworkHandler;
import communication.handler.server.serialized.SerializedBoard;
import communication.handler.server.serialized.SerializedMapChit;
import view.controller.game.BoardView;
import lwjglview.controller.board.LWJGLBoardDrawable;
import model.activity.Activity;
import model.board.Board;
import model.character.Character;
import model.controller.requests.DieRequest;
import model.counter.chit.MapChit;
import model.enums.CharacterType;
import model.enums.CounterType;
import model.enums.MapChitType;
import model.enums.SearchType;
import model.enums.TableType;
import model.enums.TileName;
import model.exceptions.MRException;
import model.interfaces.ClearingInterface;
import model.interfaces.HexTileInterface;

public interface ClientController {

	void displayMessage(String string);

	void enterBirdSong();

	void enterCharacterSelection(ArrayList<CharacterType> disabled);

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

	void setBoardView(BoardView board);

	void setCharacter(Character character);

	void checkSwordsmanTurn();

	void setAllCharacters(Map<Integer, Character> characters);

	void requestSearchInformation();

	void peekMapChits(ArrayList<MapChit> peek);
	void discoverPaths(ArrayList<String> paths);
	

	void requestSearchChoice(TableType table);

	void discoverChits(ArrayList<MapChit> peek);

	void updateMapChits(MapChitType type, ArrayList<SerializedMapChit> mapChits);

	void clueLost(MapChitType type);

	void addGold(int goldValue, MapChitType site);

	void gameFinished(CharacterType winner, int score);

	void illegalCharacterSelection(CharacterType type);

	void setEnchantedTile(TileName tile, CharacterType actor, boolean bool);

}
