package entry;

import java.io.IOException;

import controller.network.server.NetworkControllerGenerator;
import model.controller.ModelController;
import utils.resources.ResourceHandler;

public class ServerMain {
	public static void main(String[] args) {
		ResourceHandler rh = new ResourceHandler();
		try {
			ModelController model = new ModelController(rh,
					new NetworkControllerGenerator());
			model.startControl();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
