package model.player;

import java.util.ArrayList;

import model.activity.Activity;

public class PersonalHistory {
	private ArrayList<PersonalHistoryDay> days;
	private int currentDay;
	public PersonalHistory(){
		days = new ArrayList<PersonalHistoryDay>();
		days.add(new PersonalHistoryDay());
		currentDay = 1;
	}
	
	public PersonalHistory reset(){
		days = new ArrayList<PersonalHistoryDay>();
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
