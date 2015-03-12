package swingview.controller.mainmenu;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import view.controller.ViewController;

@SuppressWarnings("serial")
public class MainMenu extends JPanel implements ActionListener {
	private JButton start;
	private JButton exit;
	private ViewController parent;
	private Object clickSource = null;
	public MainMenu(ViewController par) {
		this.parent = par;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		start = new JButton("Start Game");
		exit = new JButton("Exit");
		exit.addActionListener(this);
		start.addActionListener(this);
		
		start.setAlignmentX(Component.CENTER_ALIGNMENT);
		exit.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(start);
		add(exit);
		JCheckBox cheatMode = new JCheckBox("Enable Cheat Mode");
		add(cheatMode);
		
		Thread t = new Thread(new Runnable(){
			@Override
			public void run() {
				synchronized(MainMenu.this){
					while(clickSource == null){
						try {
							MainMenu.this.wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					if(clickSource.equals(start)){
						//start game
						parent.startNetworkGame();
					}else if(clickSource.equals(exit)){
						//exit game.
						parent.exit();
					}
				}
				
			}
			
		});
		t.start();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		synchronized(this){
			clickSource = e.getSource();
			notify();
		}		
	}
}
