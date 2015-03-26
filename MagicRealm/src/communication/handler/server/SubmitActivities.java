package communication.handler.server;

import java.util.ArrayList;

import model.activity.Activity;
import server.ServerController;
import communication.ServerNetworkHandler;

public class SubmitActivities implements ServerNetworkHandler {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5900001510104132096L;
	private Iterable<Activity> activities;
	private int id;
	
	public SubmitActivities(int clientID, Iterable<Activity> activities) {
		this.activities = activities;
		id = clientID;
	}

	@Override
	public void handle(ServerController controller) {
		controller.submitActivities(id, activities);

	}

}
