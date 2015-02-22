package model.player;

import java.util.ArrayList;

import model.activity.Activity;

public class PersonalHistory {
	private ArrayList<PersonalHistoryDay> days;
	private int currentDay;
	private ArrayList<HistoryPath> paths;
	
	public ArrayList<HistoryPath> getPaths() {
		return paths;
	}

	public void addPath(HistoryPath path) {
		this.paths.add(path);
	}

	public PersonalHistory(){
		reset();
	}
	
	public PersonalHistory reset(){
		days = new ArrayList<PersonalHistoryDay>();
		days.add(new PersonalHistoryDay());
		paths = new ArrayList<HistoryPath>();
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
