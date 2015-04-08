package model.counter.chit;

import java.io.Serializable;
import java.util.ArrayList;

import communication.handler.server.serialized.SerializedMapChit;
import model.enums.MapChitType;
import model.enums.TileName;

public class MapChit extends Chit implements Serializable/*, Comparable<MapChit>*/ {
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

	public MapChit(SerializedMapChit sChit) {
		this.type = sChit.getType();
		this.identifier = sChit.getIdentifier();
		setTile(sChit.getTile());
	}

	public MapChit(MapChitType site, int clearing, TileName tile) {
		this.type = site;
		this.setClearing(clearing);
		this.setTile(tile);
	}

	public ArrayList<MapChit> getWarningAndSite() {
		return new ArrayList<MapChit>();
	}

	@Override
	public void setClearing(int clear) {
		super.setClearing(clear);
		switch (type.type()) {
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
	public String toString() {
		String str = "";
		if (this.type != null) {
			str += type + ": ";
			str += this.type.type().toString() + ": ";
			 str += this.identifier;
			// str += super.toString();
		}
		return str;
	}

	public SerializedMapChit getSerializedChit() {
		SerializedMapChit sChit = new SerializedMapChit();
		sChit.setType(type);
		sChit.setIdentifier(identifier);
		sChit.setTile(getTile());
		return sChit;
	}

	/*@Override
	public int hashCode() {
		int hash;
		hash = identifier;
		int l = MapChitType.values().length;
		hash *= l + 1;
		hash += type == null ? l : type.ordinal();
		l = TileName.values().length;
		hash *= l + 1;
		TileName tn = getTile();
		hash += tn == null ? l : tn.ordinal();
		return hash;
	}

	@Override
	public int compareTo(MapChit other) {
		int mine, theirs;
		mine = type == null ? -1 : type.ordinal();
		theirs = type == null ? -1 : other.type.ordinal();
		if (mine == theirs) {
			int delt = identifier - other.identifier;
			if(delt == 0) {
				TileName t1, t2;
				t1 = getTile();
				t2 = other.getTile();
				int a, b;
				a = t1 == null ? -1 : t1.ordinal();
				b = t2 == null ? -1 : t2.ordinal();
				return a - b;
			}
			return delt;
		}
		return mine - theirs;
	}*/

	@Override
	public boolean equals(Object other) {
		if(other instanceof MapChit) {
			MapChit o = (MapChit) other;
			return (this.identifier == o.identifier && this.type == o.type && this.getTile() == o.getTile());
		}
		return false;
	}
}
