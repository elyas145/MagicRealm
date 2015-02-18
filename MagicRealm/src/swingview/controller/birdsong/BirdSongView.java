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
import model.enums.ActivityType;
import model.player.PersonalHistory;
import controller.Controller;

@SuppressWarnings("serial")
public class BirdSongView extends JPanel implements ActionListener{
	private Controller parent;
	private PersonalHistory history;
	private JButton submit;
	ArrayList<JComboBox> boxesArray;
	public BirdSongView(Controller parent) {
		this.parent = parent;
		setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		
		history = parent.getPlayerHistory();
		HistoryView historyView = new HistoryView(history);
		historyView.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(historyView);
		
		JLabel lbl = new JLabel("CurrentDay: " + parent.getCurrentDay());
		add(lbl);
		JPanel lbls = new JPanel();
		lbls.setLayout(new BoxLayout(lbls, BoxLayout.X_AXIS));
		
		ArrayList<JLabel> lblArray = new ArrayList<JLabel>();
		for (int i = 0; i < 8; i++){
			lblArray.add(new JLabel("Phase: " + (i+1) +"          "));
			lblArray.get(i).setAlignmentX(LEFT_ALIGNMENT);
			lbls.add(lblArray.get(i));
		}
		add(lbls);
		
		boxesArray = new ArrayList<JComboBox>();
		JPanel boxes = new JPanel();
		boxes.setLayout(new BoxLayout(boxes, BoxLayout.X_AXIS));
		for(int i = 0; i < 8; i++){
			boxesArray.add(new JComboBox<Object>(ActivityType.values()));
			boxes.add(boxesArray.get(i));
		}
		add(boxes);
		submit = new JButton("Submit");
		submit.addActionListener(this);
		add(submit);
	}
	@Override
	public void actionPerformed(ActionEvent ae) {
		if(ae.getSource().equals(submit)){
			//submit to controller.
			ArrayList<String> actions = new ArrayList<String>();
			for(JComboBox<String> b : boxesArray){
				actions.add(b.getSelectedObjects()[0].toString());
			}
			
			for(int i = actions.size(); i > 0; i--){
				if(actions.get(i-1).equals(ActivityType.NONE.toString())){
					actions.remove(i-1);
				}else{
					break;
				}
			}
			
			System.out.println(actions);
			//parent.submitPhases();
		}		
	}
	
}
