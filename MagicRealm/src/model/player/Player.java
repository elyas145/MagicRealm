package model.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.EnchantedHolder;
import model.activity.Activity;
import model.character.Character;
import model.counter.chit.MapChit;
import model.enums.PathType;
import model.interfaces.ClearingInterface;

public class Player {

	private int number;
	private String name;
	private Character character = null;
	private PersonalHistory historyPad;
	private Map<ClearingInterface, EnchantedHolder<Set<ClearingInterface>>> discoveredPaths;
	private ArrayList<MapChit> discoveredChits;
	private boolean sunlightFlag = false;
	private int gold = 0;
	
	public Player(int num, String nm) {
		number = num;
		name = nm;
		character = null;
		historyPad = new PersonalHistory();

		discoveredPaths = new HashMap<ClearingInterface, EnchantedHolder<Set<ClearingInterface>>>();
		discoveredChits = new ArrayList<MapChit>();
	}

	public void setCharacter(Character c) {
		character = c;
	}

	public PersonalHistory getPersonalHistory() {
		return historyPad;
	}

	public Character getCharacter() {
		return character;
	}

	public void setActivities(List<Activity> activities) {
		for (Activity a : activities) {
			historyPad.addActivity(a);
		}

	}

	public void addDiscoveredPath(ClearingInterface cl1, ClearingInterface cl2) {
		System.out.println("Adding path for character " + character.getType());
		System.out.println("between " + cl1 + " and " + cl2);
		connectClearings(cl1, cl2, cl1.isEnchanted());
		connectClearings(cl2, cl1, cl1.isEnchanted());
		historyPad.addPath(new HistoryPath(cl1.getParentTile().getName(), cl1.getClearingNumber(), cl2.getClearingNumber(), cl1.isEnchanted(), PathType.HIDDEN));
	}

	public boolean hasDiscoveredPath(ClearingInterface cl1,
			ClearingInterface cl2) {
		if (!discoveredPaths.containsKey(cl1)) {
			return false;
		}
		return discoveredPaths.get(cl1).get(cl1.isEnchanted()).contains(cl2);
	}
	
	public void discoverMapChit(MapChit mc) {
		synchronized(discoveredChits) {
			discoveredChits.add(mc);
		}
	}
	
	public void discoverAllMapChits(Iterable<MapChit> chits) {
		for(MapChit mc: chits) {
			discoverMapChit(mc);
		}
	}
	
	public Iterable<MapChit> getDiscoveredMapChits() {
		return discoveredChits;
	}

	private void connectClearings(ClearingInterface cl1, ClearingInterface cl2,
			boolean ench) {
		if (!discoveredPaths.containsKey(cl1)) {
			discoveredPaths.put(cl1,
					new EnchantedHolder<Set<ClearingInterface>>(
							new HashSet<ClearingInterface>(),
							new HashSet<ClearingInterface>()));
		}
		discoveredPaths.get(cl1).get(ench).add(cl2);		
	}
	
	public boolean getSunLightFlag(){
		return sunlightFlag;
	}
	public void setSunlightFlag(boolean b){
		sunlightFlag = b;
	}

	public int getGold() {
		return gold;
	}

	public void addGold(int gold) {
		this.gold += gold;
	}

	public void addDiscoveredMapChit(MapChit chit) {
		discoveredChits.add(chit);
		
	}

	public boolean hasDiscoveredSite(MapChit c) {
		for(MapChit mc : discoveredChits){
			if(mc.equals(c)){
				return true;
			}
		}
		return false;
	}

}
