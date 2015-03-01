package view.selection;

public interface CursorListener {
	
	void onMove(int x, int y);
	
	void onSelection(CursorSelection select, boolean down);

}
