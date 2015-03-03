package lwjglview;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lwjglview.graphics.LWJGLGraphics;
import lwjglview.graphics.board.LWJGLBoardDrawable;
import lwjglview.selection.SelectionFrame;
import model.activity.Activity;
import model.character.Phase;
import model.enums.CharacterType;
import model.enums.TileName;
import model.player.PersonalHistory;
import view.controller.ViewController;
import view.controller.search.SearchView;

public class LWJGLClientController implements ViewController {
	
	@Override
	public void focusOnBoard(TileName selectedTile) {
		board.focusOn(selectedTile);
	}
	@Override
	public void focusOnBoard(TileName selectedTile, Integer selectedClearing) {
		board.focusOn(selectedTile, selectedClearing);
	}
	@Override
	public void setPlayerActivities(CharacterType character,
			ArrayList<Activity> activities) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setPlayerActivities(CharacterType character,
			List<Activity> activities) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void enterBirdSong(CharacterType type, int day, List<Phase> phases,
			PersonalHistory personalHistory,
			Map<TileName, List<Integer>> tileClrs) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void displayMessage(String string) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void enterMainMenu() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void enterLobby() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void enterSplashScreen() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void startNetworkGame() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void exit() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public SearchView enterSearchView(CharacterType character) {
		// TODO Auto-generated method stub
		return null;
	}

	private LWJGLBoardDrawable board;
	private SelectionFrame selections;
	private LWJGLGraphics graphics;

}
