package model.activity;

import model.controller.ModelController;
import model.enums.ActivityType;

public abstract class Activity {
	
	public Activity(ActivityType act) {
		type = act;
	}
	
	// perform the action on the model controller
	public abstract void perform(ModelController mc);
	
	private ActivityType type;
	
}
