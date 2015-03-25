package view.selection;

public abstract class PrimaryClickListener implements CursorListener {
	
	public abstract void onClick();

	@Override
	public void onMove(int x, int y) {
	}

	@Override
	public void onSelection(CursorSelection select, boolean down) {
		if(select == CursorSelection.PRIMARY && down) {
			onClick();
		}
	}

}
