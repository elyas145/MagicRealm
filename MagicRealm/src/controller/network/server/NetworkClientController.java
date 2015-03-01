package controller.network.server;

import java.util.List;

import view.controller.game.BoardView;
import network.NetworkHandler;
import network.server.Server;
import controller.ClientController;
import controller.network.server.handlers.EnterBirdSong;
import controller.network.server.handlers.EnterLobby;
import controller.network.server.handlers.InitBoard;
import controller.network.server.handlers.MessageDisplay;
import controller.network.server.handlers.ServerClosed;
import model.activity.Activity;
import model.board.Board;
import model.character.CharacterFactory;
import model.controller.ModelControlInterface;
import model.controller.requests.DieRequest;
import model.counter.chit.MapChit;
import model.enums.CharacterType;
import model.enums.CounterType;
import model.enums.TileName;
import model.exceptions.MRException;
import model.player.Player;

public class NetworkClientController {
	private Player player;
	private Server<ClientController, ModelControlInterface> server;
	private int clientID;

	public NetworkClientController(
			Server<ClientController, ModelControlInterface> serv, int clID) {
		server = serv;
		clientID = clID;

	}

	public void exit() {
		send(new ServerClosed());
	}

	public void setPlayerActivities(CharacterType player,
			List<Activity> activities) {

	}

	public void displayMessage(String string) {
		send(new MessageDisplay(string));

	}

	public void setCharacter(CharacterType character) {
		player.setCharacter(CharacterFactory.create(character));
	}

	public void setHiding(boolean hid) {
		player.getCharacter().setHiding(hid);
	}

	public void initializeBoard(NetworkHandler<BoardView> initializer) {
		server.send(new InitBoard(initializer), clientID);

	}

	public void startBirdsong() {
		send(new EnterBirdSong());
	}

	public void enterLobby() {
		send(new EnterLobby());
	}

	private void send(NetworkHandler<ClientController> handler) {
		server.send(handler, clientID);
	}

	public void enterCharacterSelection() {

	}

}
