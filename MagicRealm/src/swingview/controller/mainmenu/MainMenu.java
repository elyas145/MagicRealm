package swingview.controller.mainmenu;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import controller.Controller;
import view.controller.ViewController;
import view.controller.game.GameView;
import view.controller.mainmenu.MenuItem;
import view.controller.mainmenu.MenuItemListener;

@SuppressWarnings("serial")
public class MainMenu extends JPanel implements ActionListener {
	private JButton start;
	private JButton exit;
	private ViewController parent;
	private Object clickSource = null;
	public MainMenu(ViewController parent) {
		this.parent = parent;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		start = new JButton("Start Game");
		exit = new JButton("Exit");
		exit.addActionListener(this);
		start.addActionListener(this);
		
		start.setAlignmentX(Component.CENTER_ALIGNMENT);
		exit.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(start);
		add(exit);
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
						MainMenu.this.parent.startGameView();
					}else if(clickSource.equals(exit)){
						//exit game.
						MainMenu.this.parent.exit();
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
