package view.controller.search;

import java.util.List;

import view.controller.ItemGroup;
import model.enums.SearchType;

public interface SearchChoiceView extends ItemGroup {
	
	void selectSearchType(List<SearchType> avail, SearchTypeListener stl);
	
}
