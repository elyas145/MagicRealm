package communication.handler.server;

import model.enums.CharacterType;
import client.ClientController;
import communication.ClientNetworkHandler;

public class GameFinished implements ClientNetworkHandler {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1808179173109927103L;
	private CharacterType winner;
	private int score;
	public GameFinished(CharacterType winner, int score){
		this.winner = winner;
		this.score = score;
	}
	@Override
	public void handle(ClientController controller) {
		controller.gameFinished(winner, score);

	}

}
