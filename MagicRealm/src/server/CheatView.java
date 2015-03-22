package server;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;

public class CheatView extends Container implements ActionListener{
	private static final long serialVersionUID = -6451835899203546430L;
	private Server server;
	private JButton setTreasure;
	private JButton setSound;
	private JButton setWarning;
	private JButton start;
	private Container treasurePane;
	private Container soundPane;
	private Container warningPane;
	
	public CheatView(Server ser){
		server = ser;
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(new JLabel("CHEAT MODE"));
		setTreasure = new JButton("Set Treasure Site");
		setSound = new JButton("Set Sound");
		setWarning = new JButton("Set Warning");
		start = new JButton("Start");
		
		setTreasure.addActionListener(this);
		setSound.addActionListener(this);
		setWarning.addActionListener(this);
		start.addActionListener(this);
		
		add(setTreasure);
		add(setSound);
		add(setWarning);
		add(start);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(setTreasure)){
			//init and start the treasure pane.
			
		}else if(e.getSource().equals(setSound)){
			
		}else if(e.getSource().equals(setWarning)){
			
		}else{
			
		}
		
	}
}
