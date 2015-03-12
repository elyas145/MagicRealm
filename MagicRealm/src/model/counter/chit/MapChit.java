package model.counter.chit;

import java.io.Serializable;
import java.util.ArrayList;
import model.enums.MapChitType;
import model.enums.TileName;

public class MapChit extends Chit implements Serializable{
	private static final long serialVersionUID = -5805890352154420133L;
	private MapChitType type;
	private char identifier;
	
	public char getIdentifier() {
		return identifier;
	}

	public void setIdentifier(char identifier) {
		this.identifier = identifier;
	}

	public MapChitType getType() {
		return type;
	}

	public void setType(MapChitType type) {
		this.type = type;
	}

	public MapChit(TileName tt, MapChitType type) {
		setFlipped(false);
		setTile(tt);
		this.type = type;
		setClearing(this.type.getClearing());
	}
	
	public MapChit(MapChitType type) {
		setFlipped(false);
		this.type = type;
		setClearing(this.type.getClearing());
	}
	
	public MapChit(MapChitType type, int clearing) {
		setFlipped(false);
		this.type = type;
		setClearing(clearing);
	}
	
	public MapChit(MapChitType type, char identifier) {
		this.type = type;
		this.identifier = identifier;
	}
	
	public ArrayList<MapChit> getWarningAndSite() {
		return new ArrayList<MapChit>();
	}
	
	@Override
	public void setClearing(int clear) {
		super.setClearing(clear);
		switch(type.type()) {
		case SITE:
		case LOST_CITY:
		case LOST_CASTLE:
		case SOUND:
			setIdentifier((char) ('0' + clear));
		default:
			break;
		}
	}
	
	@Override
	public String toString(){
		String str = "";
		if(this.type != null){
			str += type.type().toString() + ": ";
			str += this.type.toString() + ". ";
			str += this.identifier + ", ";
			str += super.toString();
		}
		return str;
	}
}
