package communication.handler.client;

import model.activity.Activity;
import model.enums.CharacterType;
import server.ServerController;
import communication.ServerNetworkHandler;

public class SubmitActivities implements ServerNetworkHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7489322855060081109L;
	private Iterable<Activity> activities;
	private CharacterType type;
	
	public SubmitActivities(CharacterType type, Iterable<Activity> activities){
		this.activities = activities;
		this.type = type;
	}
	@Override
	public void handle(ServerController controller) {
		controller.submitActivities(type, activities);

	}
	@Override
	public String toString(){
		return "Submit activities handler.";
	}
}
