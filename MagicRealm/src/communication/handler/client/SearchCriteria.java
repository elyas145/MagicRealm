package communication.handler.client;

import model.enums.CharacterType;
import model.enums.TableType;
import server.ServerController;
import communication.ServerNetworkHandler;

public class SearchCriteria implements ServerNetworkHandler {
	private TableType table;
	private int rollValue;
	private CharacterType character;
	public SearchCriteria(CharacterType character, TableType table, int roll) {
		this.table = table;
		this.rollValue = roll;
		this.character = character;
	}

	@Override
	public void handle(ServerController controller) {
		controller.searchChosen(character, table, rollValue);

	}

}
