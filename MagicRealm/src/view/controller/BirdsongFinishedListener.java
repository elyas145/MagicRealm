package view.controller;

import java.util.List;

import model.enums.ActivityType;

public interface BirdsongFinishedListener {
	
	void onFinish(List<ActivityType> activities);

}
