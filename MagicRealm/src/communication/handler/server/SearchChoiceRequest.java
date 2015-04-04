package communication.handler.server;

import model.enums.TableType;
import client.ClientController;
import communication.ClientNetworkHandler;

public class SearchChoiceRequest implements ClientNetworkHandler{
	private static final long serialVersionUID = 7528140135812755047L;
	private TableType table;
	
	public SearchChoiceRequest(TableType table){
		this.table = table;
	}
	@Override
	public void handle(ClientController controller) {
		controller.requestSearchChoice(table);		
	}

}
