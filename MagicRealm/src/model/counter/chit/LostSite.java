package model.counter.chit;

import java.util.ArrayList;

import model.enums.MapChitType;;

public class LostSite extends MapChit{
	private ArrayList<MapChit> WarningAndSite;
	public ArrayList<MapChit> getWarningAndSite() {
		return WarningAndSite;
	}
	public void setWarningAndSite(ArrayList<MapChit> lostCastleChits) {
		WarningAndSite = lostCastleChits;
	}
	public LostSite(MapChitType type) {
		super(type);
	}
	
}
