package lwjglview.controller.lobby;

import lwjglview.controller.LWJGLWaitingView;
import view.controller.lobby.LobbyView;

public class LWJGLLobbyView implements LobbyView {
	
	public LWJGLLobbyView(LWJGLWaitingView notify) {
		waiting = notify;
	}

	@Override
	public void setVisible(boolean vis) {
		waiting.setText("Waiting for players");
		waiting.setVisible(true);
	}

	@Override
	public void waitingForPlayers(int number) {
		waiting.setText(String.format("Waiting for %d more players", number));
	}
	
	private LWJGLWaitingView waiting;

}
