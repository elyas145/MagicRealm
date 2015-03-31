package communication.handler.server;

import model.enums.CharacterType;
import client.ClientController;
import communication.ClientNetworkHandler;

public class UpdateHiding implements ClientNetworkHandler {
	private static final long serialVersionUID = 5173927969365623444L;
	private CharacterType character;
	private boolean hiding;
	public UpdateHiding(CharacterType character, boolean b) {
		this.character = character;
		this.hiding = b;
	}

	@Override
	public void handle(ClientController controller) {
		controller.setHiding(character, hiding);

	}

}
