package swingview.controller;

import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTable;

import model.activity.Activity;
import model.player.PersonalHistory;
import model.player.PersonalHistoryDay;

@SuppressWarnings("serial")
public class HistoryView extends JPanel {
	private PersonalHistory history;
	private JTable actions;
	private JTable points;

	public HistoryView(PersonalHistory history) {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.history = history;
		actions = new JTable(28, 8);
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
							daynum, i);
				}
			}

			daynum++;
		}
		add(actions);
	}
}
