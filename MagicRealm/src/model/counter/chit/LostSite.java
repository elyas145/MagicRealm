package model.counter.chit;

import java.util.ArrayList;

import model.enums.MapChitType;;

public class LostSite extends MapChit{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6684439001563503767L;
	private ArrayList<MapChit> WarningAndSite;
	
	@Override
	public ArrayList<MapChit> getWarningAndSite() {
		return WarningAndSite;
	}
	public void setWarningAndSite(ArrayList<MapChit> lostCastleChits) {
		WarningAndSite = lostCastleChits;
	}
	public LostSite(MapChitType type) {
		super(type);
		WarningAndSite = new ArrayList<MapChit>();
	}
	
}
