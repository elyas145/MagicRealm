package view.controller.characterselection;

import java.util.List;

import model.enums.CharacterType;

public interface CharacterSelectionView {
	
	void selectCharacter(List<CharacterType> characters, CharacterSelectionListener onselect);
	
	void disableCharacter(CharacterType character);

}
