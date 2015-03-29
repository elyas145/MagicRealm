package view.controller.birdsong;

import view.controller.ItemGroup;
import model.player.PersonalHistory;

public interface BirdsongView extends ItemGroup {
	
	void updateHistory(PersonalHistory hist);
	
	void setActivitiesListener(ActivitiesListener nal);
	
}
