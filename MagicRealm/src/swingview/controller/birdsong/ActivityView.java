package swingview.controller.birdsong;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import config.GraphicsConfiguration;
import controller.Controller;
import model.activity.Activity;
import model.activity.Move;
import model.enums.ActivityType;

@SuppressWarnings("serial")
public class ActivityView extends JFrame implements ActionListener {
	private BirdSongView parent;
	private JButton go;
	private ArrayList<MovePanel> movePanels;

	public ActivityView(BirdSongView parent, Controller controller,
			ArrayList<String> actions) {
		super("Set Activity");
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setPreferredSize(new Dimension(
				GraphicsConfiguration.INITIAL_ACTION_WIDTH * actions.size(),
				GraphicsConfiguration.INITIAL_ACTION_HEIGHT));
		setLocationRelativeTo(null);

		getContentPane().setLayout(
				new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		this.parent = parent;

		go = new JButton("Go");
		go.addActionListener(this);
		// move panels
		movePanels = new ArrayList<MovePanel>();
		JPanel moveP = new JPanel();
		moveP.setLayout(new BoxLayout(moveP, BoxLayout.X_AXIS));
		for (int i = 0; i < actions.size(); i++) {
			if (actions.get(i).equals(ActivityType.MOVE.toString())) {
				movePanels.add(new MovePanel(controller, i));
				moveP.add(movePanels.get(i));
			} else if (actions.get(i).equals(ActivityType.HIDE.toString())) {

			}
		}

		getContentPane().add(moveP);
		getContentPane().add(go);
		pack();
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(go)) {
			// go is pressed.
			// get all the activities.
			ArrayList<Activity> activities = new ArrayList<Activity>();

			for (MovePanel panel : movePanels) {
				activities.add(new Move(ActivityType.MOVE, panel
						.getSelectedTile(), panel.getSelectedClearing()));
			}
			parent.sendActivities(activities);
			this.setVisible(false);
		}
	}
}
