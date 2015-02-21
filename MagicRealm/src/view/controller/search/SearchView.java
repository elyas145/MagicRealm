package view.controller.search;

import model.enums.TableType;

public interface SearchView {

	boolean doneSearching();
	TableType getSelectedTable();

}
