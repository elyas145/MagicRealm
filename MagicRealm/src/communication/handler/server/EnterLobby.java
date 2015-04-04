package communication.handler.server;

import communication.ClientNetworkHandler;
import communication.handler.server.serialized.SerializedBoard;
import client.ClientController;

public class EnterLobby implements ClientNetworkHandler {
	private SerializedBoard board;
	
	public EnterLobby(SerializedBoard board) {
		this.board = board;
	}
	@Override
	public void handle(ClientController control) {
		control.enterLobby(board);
	}	
	@Override
	public String toString(){
		return "Enter lobby handler.";		
	}
	private static final long serialVersionUID = -53116425386558057L;

}
