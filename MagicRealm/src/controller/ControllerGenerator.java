package controller;

import controller.network.server.NetworkClientController;
import model.controller.ModelControlInterface;

public interface ControllerGenerator {

	void setModelController(ModelControlInterface mci);
	
	void rejectNew();

	NetworkClientController generateController();

}
