package communication.handler.server;

import model.enums.CharacterType;
import model.enums.TileName;
import client.ClientController;
import communication.ClientNetworkHandler;

public class UpdateEnchantedTile implements ClientNetworkHandler {
	
	private TileName tile;
	private CharacterType actor;
	private boolean bool;
	
	public UpdateEnchantedTile(TileName tile, CharacterType actor, boolean enchanted){
		this.tile = tile;
		this.actor = actor;
		this.bool = enchanted;
	}
	
	@Override
	public void handle(ClientController controller) {
		controller.setEnchantedTile(tile, actor, bool);
	}
	
	private static final long serialVersionUID = 8627259960070990063L;

}
