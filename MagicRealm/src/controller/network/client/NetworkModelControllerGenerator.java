package controller.network.client;

import network.client.Client;
import model.controller.ModelControlInterface;
import controller.ClientController;
import controller.ModelControllerGenerator;

public class NetworkModelControllerGenerator implements ModelControllerGenerator {

	@Override
	public void setController(ClientController ctrl) {
		local = ctrl;
	}
	
	@Override
	public ModelControlInterface generateModelController() {
		try {
			client = new Client<ModelControlInterface, ClientController>(local, "localhost");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		controller = new NetworkModelController(client);
		return controller;
	}

	private Client<ModelControlInterface, ClientController> client;
	
	private ClientController local;
	
	private ModelControlInterface controller;
}
