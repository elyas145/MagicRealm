package model.player;

import java.util.ArrayList;

import model.activity.Activity;

public class PersonalHistory {
	private ArrayList<PersonalHistoryDay> days;
	private int currentDay;
	public PersonalHistory(){
		days = new ArrayList<PersonalHistoryDay>();
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
	}

	public int getCurrentDay() {
		return currentDay;
	}
}
