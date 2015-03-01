package controller;

import model.controller.ModelControlInterface;

public interface ModelControllerGenerator {

	void setController(ClientController ctrl);
	
	public ModelControlInterface generateModelController();

}
