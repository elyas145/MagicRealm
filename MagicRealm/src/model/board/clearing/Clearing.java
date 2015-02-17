package model.board.clearing;

import java.nio.FloatBuffer;
import java.util.List;

import model.counter.chit.Chit;
import model.interfaces.ClearingInterface;

import utils.math.Point;

public class Clearing implements ClearingInterface {
	private Point location;
	private Point location_e;

	public Object getLocation_e() {
		return location_e;
	}

	public void setLocation_e(Point location_e) {
		this.location_e = location_e;
		// System.out.println("Set Location_e: " + location_e.toSring());
	}

	private List<Chit> chits;
	private int number;

	public Point getLocation() {
		return location;
	}

	public void setLocation(Point location) {
		this.location = location;
		// System.out.println("Set Location: " + location.toSring());
	}

	public List<Chit> getChits() {
		return chits;
	}

	public void setChits(List<Chit> chits) {
		this.chits = chits;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	@Override
	public int getClearingNumber() {
		return number;
	}

	@Override
	public void getTilePosition(boolean enchanted, FloatBuffer dest) {
		if (enchanted) {
			dest.put(0, location_e.getX());
			dest.put(1, location_e.getY());
		} else {
			dest.put(0, location.getX());
			dest.put(1, location.getY());
		}
	}

}
