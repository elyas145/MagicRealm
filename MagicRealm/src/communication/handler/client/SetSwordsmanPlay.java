package communication.handler.client;

import server.ServerController;
import communication.ServerNetworkHandler;

public class SetSwordsmanPlay implements ServerNetworkHandler {
	private static final long serialVersionUID = 6619288721160628363L;
	private boolean turn;

	public SetSwordsmanPlay(boolean turn) {
		this.turn = turn;
	}

	@Override
	public void handle(ServerController controller) {
		controller.setSwordsManTurn(turn);
	}
	@Override
	public String toString(){
		return "set swords man play to " + turn + ".";
	}
}
