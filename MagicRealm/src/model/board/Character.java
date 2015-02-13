package model.board;

import java.util.ArrayList;

import model.enums.CharacterType;
import model.enums.SiteType;
import model.interfaces.CharacterInterface;

public class Character implements CharacterInterface {
	private CharacterType type;
	private SiteType initialLocation;
	private boolean hiding;
	private ArrayList<Chit> belongings;
}
