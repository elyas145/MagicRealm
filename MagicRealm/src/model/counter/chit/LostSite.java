package model.counter.chit;

import java.util.ArrayList;

import model.enums.MapChitType;;

public class LostSite extends MapChit{
	private ArrayList<MapChit> WarningAndSite;
	
	@Override
	public ArrayList<MapChit> getWarningAndSite() {
		return WarningAndSite;
	}
	public void setWarningAndSite(ArrayList<MapChit> lostCastleChits) {
		WarningAndSite = lostCastleChits;
		for(MapChit mc: WarningAndSite) {
			mc.setTile(getTile());
		}
	}
	public LostSite(MapChitType type) {
		super(type);
	}
	
}
