package swingview.controller.birdsong;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import swingview.controller.HistoryView;
import view.controller.ViewController;
import model.activity.Activity;
import model.activity.Empty;
import model.activity.Hide;
import model.activity.Search;
import model.character.Phase;
import model.enums.ActivityType;
import model.enums.CharacterType;
import model.enums.PhaseType;
import model.enums.TileName;
import model.player.PersonalHistory;
import model.player.Player;

@SuppressWarnings("serial")
public class BirdSongView extends JPanel implements ActionListener {
	private ViewController parent;
	private PersonalHistory history;
	private JButton submit;
	private List<JComboBox<Object>> boxesArray;
	private ArrayList<String> actions = new ArrayList<String>();
	private CharacterType character;
	private ActivityView activityView;

	public BirdSongView(ViewController parent, CharacterType chr, int day,
			List<Phase> phases, PersonalHistory hist,
			Map<TileName, List<Integer>> tileClrs) {
		this.parent = parent;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		character = chr;
		JLabel lblPlayer = new JLabel(character.toString());
		add(lblPlayer);
		history = hist;
		HistoryView historyView = new HistoryView(history);
		historyView.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(historyView);

		JLabel lbl = new JLabel("CurrentDay: " + day);
		add(lbl);

		JPanel wrapper = new JPanel();
		wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.X_AXIS));

		boxesArray = new ArrayList<JComboBox<Object>>();
		for (Phase phase : phases) {
			JPanel pane = new JPanel();
			pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
			switch (phase.getType()) {
			case SUNLIGHT:
				lbl = new JLabel("Sunlight Phase");
				break;
			case SPECIAL:
				lbl = new JLabel("Special Phase");
				break;
			default:
				lbl = new JLabel("Default Phase");
				break;
			}
			pane.setAlignmentX(Component.LEFT_ALIGNMENT);
			pane.add(lbl);
			JComboBox<Object> tmp = new JComboBox<Object>(phase
					.getPossibleActivities().toArray());
			boxesArray.add(tmp);
			pane.add(tmp);
			wrapper.add(pane);
		}
		add(wrapper);
		submit = new JButton("Submit");
		submit.addActionListener(this);
		add(submit);
		activityView = new ActivityView(this, parent, actions, character, tileClrs);
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource().equals(submit)) {
			// get activities to array
			for (JComboBox<Object> b : boxesArray) {
				actions.add(b.getSelectedItem().toString());
			}
			// trim none activities from end.
			while (actions.size() > 0
					&& actions.get(actions.size() - 1).equals(
							ActivityType.NONE.toString())) {
				actions.remove(actions.size() - 1);
			}

			// check if no activities were set.
			if (!actions.isEmpty()) {
				if (actions.contains(ActivityType.MOVE.toString())) {
					// setup move activities if needed.
					activityView.show();
				} else {
					String[] arr = new String[actions.size()];
					for (int i = 0; i < arr.length; i++) {
						arr[i] = actions.get(i);
					}
					sendActivities(arr);
				}

			} else {
				sendActivities(new String[0]);
			}
		}
	}

	public void sendActivities(List<Activity> moveActivities) {
		int moveCounter = 0;
		ArrayList<Activity> activities = new ArrayList<Activity>();
		for (String action : actions) {
			ActivityType act = ActivityType.valueOf(action);
			switch (act) {
			case MOVE:
				activities.add(moveActivities.get(moveCounter));
				moveCounter++;
				break;
			case HIDE:
				activities.add(new Hide(character));
				break;
			case SEARCH:
				activities.add(new Search(character));
				break;
			default:
				break;
			}
		}
		parent.setPlayerActivities(character, activities);
	}

	public void sendActivities(String[] activitiesArr) {
		List<Activity> activities = new ArrayList<Activity>();
		CharacterType chr = character;
		for (String action : activitiesArr) {
			switch (ActivityType.valueOf(action)) {
			case HIDE:
				activities.add(new Hide(chr));
				break;
			case SEARCH:
				activities.add(new Search(chr));
				break;
			case NONE:
				activities.add(new Empty(chr));
				break;
			default:
				break;
			}
		}
		parent.setPlayerActivities(character, activities);
	}

}
