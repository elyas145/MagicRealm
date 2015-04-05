package view.controller.cheatmode;

import view.controller.ItemGroup;

public interface DieSelectionView extends ItemGroup {
	
	void selectDie(DieSelectionListener onselect);

}
