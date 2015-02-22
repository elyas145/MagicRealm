package swingview.controller;

import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;

import model.activity.Activity;
import model.enums.PathType;
import model.enums.TileName;
import model.player.HistoryPath;
import model.player.PersonalHistory;
import model.player.PersonalHistoryDay;

@SuppressWarnings("serial")
public class HistoryView extends JPanel {
	private JTable actions;

	public HistoryView(PersonalHistory history) {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		actions = new JTable(28, 6);
		ArrayList<PersonalHistoryDay> days = history.getDays();

		int daynum = 0;
		for (int i = 0; i < 28; i++) {
			actions.setValueAt("" + (i + 1), i, 0);
		}
		for (PersonalHistoryDay d : days) {
			ArrayList<Activity> activities = d.getActivities();
			if (activities != null) {
				for (int i = 0; i < activities.size(); i++) {

					actions.setValueAt(activities.get(i).getType().toString(),
							daynum - 1, i + 1);
				}
			}
			daynum++;
		}
		add(actions);
		JPanel discoveryPanel = new JPanel();
		discoveryPanel
				.setLayout(new BoxLayout(discoveryPanel, BoxLayout.Y_AXIS));
		if (!history.getPaths().isEmpty()) {
			discoveryPanel.add(new JLabel("Discoveries:"));
			discoveryPanel.add(new JLabel("Hidden Paths:"));
			for(TileName tile : history.getPaths().keySet()){
				for (HistoryPath path : history.getPaths().get(tile)) {
					if(path.getType() == PathType.HIDDEN){
						JLabel lbl = new JLabel();
						String lblString = path.getTile().toString();
						if (path.isEnchanted()) {
							lblString += "(enchanted)";
						} else {
							lblString += "(green)";
						}
						lblString += path.getClearingOne() + "-"
								+ path.getClearingTwo();
						lbl.setText(lblString);
						discoveryPanel.add(lbl);
						add(discoveryPanel);
					}					
				}
			}
			
		}
	}
}
