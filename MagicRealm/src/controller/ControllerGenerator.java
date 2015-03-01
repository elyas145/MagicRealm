package controller;

import model.controller.ModelControlInterface;

public interface ControllerGenerator {

	void setModelController(ModelControlInterface mci);
	
	void rejectNew();

	ClientController generateController();

}
