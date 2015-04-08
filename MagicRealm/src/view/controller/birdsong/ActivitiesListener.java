package view.controller.birdsong;

import java.util.List;

import model.enums.ActivityType;

public interface ActivitiesListener {
	
	void onActivitiesChosen(List<ActivityType> acts);
	
}
