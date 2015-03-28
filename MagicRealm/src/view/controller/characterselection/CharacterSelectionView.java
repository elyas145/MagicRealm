package view.controller.characterselection;

import java.util.List;

import utils.handler.Handler;
import model.enums.CharacterType;

public interface CharacterSelectionView {
	
	void selectCharacter(List<CharacterType> characters, Handler<CharacterType> onselect);

}
