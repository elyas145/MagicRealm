package model.board;

import java.util.List;

import utils.math.Point;

public class Clearing {
	private Point location;
	private Point location_e;

	public Point getLocation_e() {
		return location_e;
	}

	public void setLocation_e(Point location_e) {
		this.location_e = location_e;
		//System.out.println("Set Location_e: " + location_e.toSring());
	}

	private List<Chit> chits;
	private int number;

	public Point getLocation() {
		return location;
	}

	public void setLocation(Point location) {
		this.location = location;
		//System.out.println("Set Location: " + location.toSring());
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

}
