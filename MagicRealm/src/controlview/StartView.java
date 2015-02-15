package controlview;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.SplashScreen;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;

import config.GraphicsConfiguration;
import controller.ControllerMain;

@SuppressWarnings("serial")
public class StartView extends JFrame implements ActionListener {
	static Toolkit tk = Toolkit.getDefaultToolkit();
	static int xSize = ((int) tk.getScreenSize().getWidth());
	static int ySize = ((int) tk.getScreenSize().getHeight());
	
	static void renderSplashFrame(Graphics2D g, int frame) {
		final String[] comps = { "objects", "models", "characters", "board"};
		g.setComposite(AlphaComposite.Clear);
		g.fillRect(0, 0, xSize, ySize);
		g.setPaintMode();
		g.setColor(Color.RED);
		g.setFont(new Font("Castellar", Font.ITALIC, 32)); 
		g.drawString("Loading " + comps[(frame / 8) % 3] + "...", (xSize/2)-100, ySize/2);
	}

	public StartView() {
		super("Magic Realm");
		setUndecorated(true);
		setSize(xSize, ySize);
		setLayout(new BorderLayout());

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
		splash.close();
		this.getContentPane().setBackground(Color.BLACK);
		
		//character selection.
		
		setVisible(true);
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

	public static void main(String args[]) {
		StartView test = new StartView();
	}
}
