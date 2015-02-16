package view.controller.birdsong;

import model.player.PersonalHistory;

public interface BirdsongView {
	
	void updateHistory(PersonalHistory hist);
	
	void onNewActivity(NewActivityListener nal);
	
}
