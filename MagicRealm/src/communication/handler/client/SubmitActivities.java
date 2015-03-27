package communication.handler.client;

import model.activity.Activity;
import server.ServerController;
import communication.ServerNetworkHandler;

public class SubmitActivities implements ServerNetworkHandler {

	private Iterable<Activity> activities;
	private int id;
	
	public SubmitActivities(int id, Iterable<Activity> activities){
		this.activities = activities;
		this.id = id;
	}
	@Override
	public void handle(ServerController controller) {
		controller.submitActivities(id, activities);

	}

}
