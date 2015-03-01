package controller.network.client;

import java.util.List;

import controller.ClientController;
import network.client.Client;
import model.activity.Activity;
import model.controller.ModelControlInterface;
import model.enums.CharacterType;
import model.enums.PeerType;
import model.enums.TableType;
import model.enums.TileName;
import model.exceptions.GameFullException;

public class NetworkModelController implements ModelControlInterface {
	
	public NetworkModelController(Client<ModelControlInterface, ClientController> cl) {
		client = cl;
		client.start();
	}

	@Override
	public void moveCharacter(CharacterType characterType, TileName tt,
			int clearing) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCharacterHidden(CharacterType character, boolean hid) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void killCharacter(CharacterType character) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPlayerActivities(List<Activity> activities, CharacterType chr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void performSearch(TableType selectedTable, CharacterType chr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void peerChoice(PeerType choice, CharacterType actor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hideCharacter(CharacterType actor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startSearching(CharacterType actor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hideCharacter(int chance, CharacterType character) {
		// TODO Auto-generated method stub
		
	}
	
	private Client<ModelControlInterface, ClientController> client;

}
