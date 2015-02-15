package controlview;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.SplashScreen;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.border.Border;

import utils.resources.ResourceHandler;
import config.GameConfiguration;
import config.GraphicsConfiguration;
import controller.ControllerMain;

@SuppressWarnings("serial")
public class StartView extends JFrame implements ActionListener, MouseListener {
	private ControllerMain parent;
	private static Toolkit tk = Toolkit.getDefaultToolkit();
	private static int xSize = ((int) tk.getScreenSize().getWidth());
	private static int ySize = ((int) tk.getScreenSize().getHeight());
	private JLabel[] charCards;

	private static void renderSplashFrame(Graphics2D g, int frame) {
		final String[] comps = { "objects", "models", "characters", "board" };
		g.setComposite(AlphaComposite.Clear);
		g.fillRect(0, 0, xSize, ySize);
		g.setPaintMode();
		g.setColor(Color.WHITE);
		g.setFont(new Font("Castellar", Font.ITALIC, 32));
		g.drawString("Loading " + comps[(frame / 8) % 3] + "...",
				(xSize / 2) - 500, ySize / 2);
	}

	public StartView(ControllerMain parent) {
		super("Magic Realm");

		// init variables
		charCards = new JLabel[GameConfiguration.MAX_PLAYERS];
		setUndecorated(true);
		setSize(xSize, ySize);
		setLayout(new BoxLayout(this.getContentPane(), BoxLayout.X_AXIS));

		// just for testing.
		Menu m1 = new Menu("File");
		MenuItem mi1 = new MenuItem("Exit");
		m1.add(mi1);
		mi1.addActionListener(this);
		this.addWindowListener(closeWindow);

		MenuBar mb = new MenuBar();
		setMenuBar(mb);
		mb.add(m1);

		final SplashScreen splash = SplashScreen.getSplashScreen();
		if (splash == null) {
			System.out.println("SplashScreen.getSplashScreen() returned null");
			return;
		}

		Graphics2D g = splash.createGraphics();
		if (g == null) {
			System.out.println("g is null");
			return;
		}
		Rectangle2D r = new Rectangle2D.Double(0, 0, xSize, ySize);
		for (int i = 0; i < GraphicsConfiguration.SPLASH_TIME; i++) {
			g.setPaint(Color.BLACK);
			g.fill(r);
			renderSplashFrame(g, i);
			splash.update();
			try {
				Thread.sleep(90);
			} catch (InterruptedException e) {
			}
		}
		this.getContentPane().setBackground(Color.BLACK);
		// character selection.

		// title
		JLabel title = new JLabel("Select Characters!");
		title.setForeground(Color.WHITE);
		title.setFont(new Font("Castellar", Font.PLAIN, 32));
		title.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.getContentPane().add(title);
		this.getContentPane().add(Box.createVerticalGlue());

		// character cards.
		// labels with pics in them. once clicked, pic will change to the
		// reverse side of the card.
		for (int i = 0; i < GameConfiguration.MAX_PLAYERS; i++) {
			charCards[i] = new JLabel();
			try {
				charCards[i].setIcon(new ImageIcon(parent.getRh().getResource(
						ResourceHandler.joinPath("images", "cards",
								parent.getAllCharacters().get(i).getType().name()+"_F.jpg"))));
				System.out.println(parent.getRh().getResource(
						ResourceHandler.joinPath("images", "cards",
								parent.getAllCharacters().get(i).getType().name()+"_F.jpg")));
				charCards[i].setAlignmentY(TOP_ALIGNMENT);
				charCards[i].setAlignmentY(LEFT_ALIGNMENT);
				charCards[i].addMouseListener(this);
				this.getContentPane().add(charCards[i]);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		splash.close();
		// setVisible(true);
		toFront();
	}

	public void start() {
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		System.exit(0);

	}

	private static WindowListener closeWindow = new WindowAdapter() {
		public void windowClosing(WindowEvent e) {
			e.getWindow().dispose();
		}
	};

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent me) {
		charCards[0].setBorder(BorderFactory.createLineBorder(Color.BLUE, 5));
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
