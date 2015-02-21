package model.counter.chit;

import java.util.ArrayList;

import model.enums.MapChitType;
import model.enums.TileName;

public class LostSite extends MapChit{
	private ArrayList<MapChitType> WarningAndSite;
	public ArrayList<MapChitType> getWarningAndSite() {
		return WarningAndSite;
	}
	public void setWarningAndSite(ArrayList<MapChitType> warningAndSite) {
		WarningAndSite = warningAndSite;
	}
	public LostSite(TileName tt, MapChitType type) {
		super(tt, type);
		WarningAndSite = new ArrayList<MapChitType>();
	}
	
}
