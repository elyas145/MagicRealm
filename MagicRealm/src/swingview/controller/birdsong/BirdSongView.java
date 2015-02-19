package swingview.controller.birdsong;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import swingview.controller.HistoryView;
import model.activity.Activity;
import model.activity.Empty;
import model.character.Phase;
import model.enums.ActivityType;
import model.enums.PhaseType;
import model.player.PersonalHistory;
import model.player.Player;
import controller.Controller;

@SuppressWarnings("serial")
public class BirdSongView extends JPanel implements ActionListener {
	private Controller parent;
	private PersonalHistory history;
	private JButton submit;
	ArrayList<JComboBox<Object>> boxesArray;
	private ArrayList<String> actions = new ArrayList<String>();

	public BirdSongView(Controller parent, String player, ArrayList<Phase> phases) {
		this.parent = parent;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		JLabel lblPlayer = new JLabel(player);
		add(lblPlayer);
		history = parent.getPlayerHistory();
		HistoryView historyView = new HistoryView(history);
		historyView.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(historyView);

		JLabel lbl = new JLabel("CurrentDay: " + parent.getCurrentDay());		
		add(lbl);
		
		JPanel lbls = new JPanel();
		JPanel boxes = new JPanel();
		lbls.setLayout(new BoxLayout(lbls, BoxLayout.X_AXIS));
		boxes.setLayout(new BoxLayout(boxes, BoxLayout.X_AXIS));
		
		ArrayList<JLabel> lblArray = new ArrayList<JLabel>();
		boxesArray = new ArrayList<JComboBox<Object>>();
		for (int i = 0; i < phases.size(); i++) {
			if(phases.get(i).getType() == PhaseType.DEFAULT){
				lblArray.add(new JLabel("Default Phase: " + (i + 1) + "           "));
			}else if(phases.get(i).getType() == PhaseType.SPECIAL){
				lblArray.add(new JLabel("Special Phase: " + (i + 1) + "           "));
			}else{
				lblArray.add(new JLabel("Sunlight Phase: " + (i + 1) + "          "));
			}			
			lblArray.get(i).setAlignmentX(BOTTOM_ALIGNMENT);
			lbls.add(lblArray.get(i));
			
			boxesArray.add(new JComboBox<Object>(phases.get(i).getPossibleActivities().toArray()));
			boxes.add(boxesArray.get(i));
		}
		add(lbls);
		add(boxes);
		submit = new JButton("Submit");
		submit.addActionListener(this);
		add(submit);
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource().equals(submit)) {
			// submit to controller.
			for (JComboBox<Object> b : boxesArray) {
				actions.add(b.getSelectedItem().toString());
			}

			for (int i = actions.size(); i > 0; i--) {
				if (actions.get(i - 1).equals(ActivityType.NONE.toString())) {
					actions.remove(i - 1);
				} else {
					break;
				}
			}
			if (!actions.isEmpty()) {
				ActivityView activityView = new ActivityView(this, parent,
						actions);
			} else {
				String[] arr = new String[actions.size()];
				for(int i = 0; i < arr.length; i++){
					arr[i] = actions.get(i);
				}
				sendActivities(arr);
			}
		}
	}

	public void sendActivities(ArrayList<Activity> moveActivities) {
		int moveCounter = 0;
		ArrayList<Activity> activities = new ArrayList<Activity>();
		for (String action : actions) {
			if (action.equals(ActivityType.MOVE.toString())) {
				activities.add(moveActivities.get(moveCounter));
				moveCounter++;
			} else {
				activities.add(new Empty(ActivityType.NONE));
			}
		}
		parent.setCurrentPlayerActivities(activities);
	}

	public void sendActivities(String[] activitiesArr) {
		ArrayList<Activity> activities = new ArrayList<Activity>();
		for (String action : activitiesArr) {
			activities.add(new Empty(ActivityType.NONE));

		}
		parent.setCurrentPlayerActivities(activities);
	}

}
