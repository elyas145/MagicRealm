package server;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import config.GameConfiguration;
import config.GraphicsConfiguration;

public class ServerView extends JFrame implements ActionListener{

	private static final long serialVersionUID = -8228884624338159162L;
	private Server server;
	private Toolkit tk = Toolkit.getDefaultToolkit();
	private int xSize = ((int) tk.getScreenSize().getWidth());
	private int ySize = ((int) tk.getScreenSize().getHeight());
	private JButton yes;
	private JButton no;
	
	public ServerView(Server ser) {
		super("Magic Realm Server");
		server = ser;
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setPreferredSize(new Dimension(400, 100));
		setLocationRelativeTo(null);
		setLocation(xSize / 2 + GraphicsConfiguration.INITIAL_WINDOW_WIDTH / 2,
				ySize / 2 - GraphicsConfiguration.INITIAL_MAIN_HEIGHT / 2);
		
		this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		this.getContentPane().add(new JLabel("Would you like to start the server in cheat mode?"));
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		yes = new JButton("Yes");
		no = new JButton("No");
		yes.addActionListener(this);
		no.addActionListener(this);
		panel.add(yes);
		panel.add(no);
		panel.setAlignmentY(CENTER_ALIGNMENT);
		getContentPane().add(panel);
		pack();
	}

	public void start() {
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {		
		if(e.getSource().equals(yes)){
			//show the cheat mode view.
			GameConfiguration.Cheat = true;
			setVisible(false);
			new CheatView(server);
			server.startServer();
			pack();
		}else{
			JOptionPane.showMessageDialog(this, "This window will now close, and the server will start without cheat mode.");
			this.setVisible(false);
			server.doneSettingCheatMode();
			server.startServer();
		}
		
	}
}
