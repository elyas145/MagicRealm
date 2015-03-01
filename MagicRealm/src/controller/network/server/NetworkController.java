package controller.network.server;

import java.util.List;

import view.controller.game.BoardView;
import network.NetworkHandler;
import network.server.Server;
import controller.ClientController;
import controller.network.server.handlers.EnterLobby;
import controller.network.server.handlers.InitBoard;
import controller.network.server.handlers.MessageDisplay;
import controller.network.server.handlers.ServerClosed;
import model.activity.Activity;
import model.board.Board;
import model.controller.ModelControlInterface;
import model.controller.requests.DieRequest;
import model.counter.chit.MapChit;
import model.enums.CharacterType;
import model.enums.CounterType;
import model.enums.TileName;
import model.exceptions.MRException;

public class NetworkController implements ClientController {

	public NetworkController(Server<ClientController, ModelControlInterface> serv,
			int clID) {
		server = serv;
		clientID = clID;
	}

	@Override
	public void onSplashScreenEnd() {
		// TODO Auto-generated method stub

	}

	@Override
	public void exit() {
		send(new ServerClosed());
	}

	@Override
	public void startGameView() {
		// TODO enter lobby?
	}

	@Override
	public void setPlayerActivities(CharacterType player, List<Activity> activities) {
		// TODO Auto-generated method stub

	}

	@Override
	public void startGame() {
		// TODO Auto-generated method stub

	}

	@Override
	public void displayMessage(String string) {
		send(new MessageDisplay(string));

	}

	@Override
	public void setCurrentCharacter(CharacterType character) {
		// TODO Auto-generated method stub

	}

	@Override
	public void moveCounter(CounterType counter, TileName tt, int clearing) {
		// TODO Auto-generated method stub

	}

	@Override
	public void startSearch(CharacterType actor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void focusOnBoard(TileName tile) {
		// TODO Auto-generated method stub

	}

	@Override
	public void focusOnBoard(TileName tile, int clearing) {
		// TODO Auto-generated method stub

	}

	@Override
	public void focusOnBoard(CounterType counter) {
		// TODO Auto-generated method stub

	}

	@Override
	public void revealMapChits(Iterable<MapChit> chits) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setHiding(CharacterType character, boolean hid) {
		// TODO Auto-generated method stub

	}

	@Override
	public void raiseException(MRException exception) {
		// TODO Auto-generated method stub

	}

	@Override
	public void performPeerChoice() {
		// TODO Auto-generated method stub

	}

	@Override
	public void rollDie(CharacterType actor, DieRequest peerTable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void initializeBoard(NetworkHandler<BoardView> initializer) {
		server.send(new InitBoard(initializer), clientID);

	}

	@Override
	public BoardView startBoardView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void startBirdsong() {
		// TODO Auto-generated method stub

	}

	@Override
	public void enterLobby() {
		send(new EnterLobby());
	}

	private void send(NetworkHandler<ClientController> handler) {
		server.send(handler, clientID);
	}

	private Server<ClientController, ModelControlInterface> server;

	private int clientID;

}
