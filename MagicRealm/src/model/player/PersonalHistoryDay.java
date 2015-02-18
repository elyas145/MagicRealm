package model.player;

import java.util.ArrayList;

import model.activity.Activity;

public class PersonalHistoryDay {
	private ArrayList<Activity> activities;
	private int fame;
	private int notoriety;
	private int gold;

	public ArrayList<Activity> getActivities() {
		return activities;
	}

	public int getFame() {
		return fame;
	}

	public void setFame(int fame) {
		this.fame = fame;
	}

	public int getNotoriety() {
		return notoriety;
	}

	public void setNotoriety(int notoriety) {
		this.notoriety = notoriety;
	}

	public int getGold() {
		return gold;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}

	public void addActivity(Activity a){
		activities.add(a);
	}
}
