package model.counter.chit;

import java.io.Serializable;
import java.util.ArrayList;

import communication.handler.server.serialized.SerializedMapChit;
import model.enums.MapChitType;
import model.enums.TileName;

public class MapChit extends Chit implements Serializable, Comparable<MapChit> {
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
			str += type.type().toString() + ": ";
			str += this.type.toString() + ". ";
			str += this.identifier + ", ";
			str += super.toString();
		}
		return str;
	}

	@Override
	public int hashCode() {
		return type.hashCode() + identifier * MapChitType.values().length;
	}

	public SerializedMapChit getSerializedChit() {
		SerializedMapChit sChit = new SerializedMapChit();
		sChit.setType(type);
		sChit.setIdentifier(identifier);
		sChit.setTile(getTile());
		return sChit;
	}

	@Override
	public int compareTo(MapChit other) {
		int mine, theirs;
		mine = type.ordinal();
		theirs = other.type.ordinal();
		if(mine == theirs) {
			return identifier - other.identifier;
		}
		return mine - theirs;
	}
}
