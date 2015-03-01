package swingview;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import network.NetworkHandler;
import controller.ClientController;
import controller.ModelControllerGenerator;
import lwjglview.graphics.LWJGLGraphics;
import lwjglview.graphics.board.LWJGLBoardDrawable;
import model.activity.Activity;
import model.character.Character;
import model.character.CharacterFactory;
import model.character.Phase;
import model.controller.ModelControlInterface;
import model.controller.requests.DieRequest;
import model.counter.chit.MapChit;
import model.enums.CharacterType;
import model.enums.CounterType;
import model.enums.TileName;
import model.exceptions.MRException;
import model.player.Player;
import utils.resources.ResourceHandler;
import view.controller.ViewController;
import view.controller.game.BoardView;
import view.controller.search.SearchView;

public class ControllerMain implements ClientController {

	private LWJGLGraphics gfx;
	private ResourceHandler rh;
	private ViewController mainView;
	private BoardView boardView;
	private ModelControllerGenerator modelGenerator;
	private ModelControlInterface model;
	private CharacterType player;

	public ControllerMain(ViewController view) {
		rh = new ResourceHandler();
		mainView = view;
		gfx = new LWJGLGraphics(rh, this);
	}
	
	public void setModelControllerGenerator(ModelControllerGenerator mcg) {
		modelGenerator = mcg;
		modelGenerator.setController(this);
	}

	public void start() {
		model = modelGenerator.generateModelController();
	}

	@Override
	public BoardView startBoardView() {
		LWJGLBoardDrawable boardDrawable;
		try {
			boardDrawable = new LWJGLBoardDrawable(rh);
			boardView = boardDrawable;
			gfx.addDrawable(boardDrawable);
			return boardView;
		} catch (IOException e1) {
			throw new RuntimeException(e1);
		}
	}

	public ArrayList<Character> getAllCharacters() {
		return CharacterFactory.getPossibleCharacters();
	}

	public ResourceHandler getRh() {
		return rh;
	}

	public void goToMainMenu() {
		mainView.enterMainMenu();
	}

	@Override
	public void onSplashScreenEnd() {
		goToMainMenu();
	}

	@Override
	public void exit() {
		String ObjButtons[] = { "Yes", "No" };
		int PromptResult = JOptionPane.showOptionDialog(null,
				"Are you sure you want to exit?", "Magic Realm",
				JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null,
				ObjButtons, ObjButtons[1]);
		if (PromptResult == JOptionPane.YES_OPTION) {
			System.exit(0);
		}
	}

	@Override
	public void startGameView() {
		startBoardView();
	}

	private void sendActivities(List<Activity> activities) {
		model.setPlayerActivities(activities, player);
	}

	private void beginBoardTurn(Player plr) {
		boardView.focusOn(plr.getCharacter().getType().toCounter());
		boardView.hideAllMapChits();
	}

	private void startBirdSong(Player player, int day, List<Phase> phases, Map<TileName, List<Integer>> tileClrs) {
		mainView.enterBirdSong(player.getCharacter().getType(), day, phases,
				player.getPersonalHistory(), tileClrs);
	}

	@Override
	public void displayMessage(String string) {
		mainView.displayMessage(string);
	}

	@Override
	public void revealMapChits(Iterable<MapChit> chits) {
		boardView.revealAllMapChits(chits);
	}

	@Override
	public void setCurrentCharacter(CharacterType character) {
		focusOnBoard(character.toCounter());
	}

	@Override
	public void moveCounter(CounterType counter, TileName tt, int clearing) {
		boardView.setCounter(counter, tt, clearing);
	}

	@Override
	public void startSearch(CharacterType searcher) {
		SearchView sv = mainView.enterSearchView(searcher);
		synchronized (sv) {
			while (!sv.doneSearching()) {
				try {
					sv.wait();
				} catch (InterruptedException e) {
				}
			}
			model.performSearch(sv.getSelectedTable(), searcher);
		}
	}

	@Override
	public void focusOnBoard(TileName tile) {
		boardView.focusOn(tile);
	}

	@Override
	public void focusOnBoard(TileName tile, int clearing) {
		boardView.focusOn(tile, clearing);
	}

	@Override
	public void focusOnBoard(CounterType counter) {
		boardView.focusOn(counter);
	}

	@Override
	public void setHiding(CharacterType character, boolean hid) {
		// TODO Auto-generated method stub

	}

	@Override
	public void raiseException(MRException exception) {
		mainView.displayMessage(exception.getMessage());
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
		// TODO Auto-generated method stub

	}

	@Override
	public void setPlayerActivities(CharacterType character, List<Activity> activities) {
		model.setPlayerActivities(activities, character);
	}

	@Override
	public void startGame() {
	}

	@Override
	public void startBirdsong() {
		// TODO Auto-generated method stub

	}

	@Override
	public void enterLobby() {
		// TODO enter lobby view
		System.out.println("TODO: enter client lobby view");
		gfx.start();
	}

}
