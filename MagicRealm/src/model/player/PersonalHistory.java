package model.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import model.activity.Activity;
import model.enums.TileName;

public class PersonalHistory {
	private ArrayList<PersonalHistoryDay> days;
	private int currentDay;
	private Map<TileName, Set<HistoryPath>> paths;
	
	public Map<TileName, Set<HistoryPath>> getPaths() {
		return paths;
	}

	public void addPath(HistoryPath path) {
		if(paths.get(path.getTile()) == null){
			paths.put(path.getTile(), new HashSet<HistoryPath>());
		}
		this.paths.get(path.getTile()).add(path);
	}

	public PersonalHistory(){
		reset();
	}
	
	public PersonalHistory reset(){
		days = new ArrayList<PersonalHistoryDay>();
		days.add(new PersonalHistoryDay());
		paths = new HashMap<TileName, Set<HistoryPath>>();
		currentDay = 1;
		return this;
	}
	public ArrayList<PersonalHistoryDay> getDays(){
		return new ArrayList<PersonalHistoryDay>(days);
	}
	public void addActivity(Activity a){
		days.get(currentDay-1).addActivity(a);
	}
	public void newDay(){
		currentDay++;
		days.add(new PersonalHistoryDay());
	}

	public int getCurrentDay() {
		return currentDay;
	}

	public ArrayList<Activity> getCurrentActivities() {
		return days.get(currentDay-1).getActivities();
	}
	
	
}
