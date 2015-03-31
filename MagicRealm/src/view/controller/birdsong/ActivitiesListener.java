package view.controller.birdsong;

import java.util.List;

import model.activity.Activity;
import model.enums.ActivityType;

public interface ActivitiesListener {
	
	void onActivitiesChosen(List<ActivityType> acts);
	
}
