package swingview;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import swingview.controller.mainmenu.MainMenu;
import utils.time.Timing;
import view.controller.ViewController;
import view.controller.mainmenu.MainMenuView;
import view.controller.splashscreen.SplashScreen;
import config.GraphicsConfiguration;
import controller.Controller;

@SuppressWarnings("serial")
public class MainView extends JFrame implements ViewController, WindowListener {
	private Controller parent;

	private static Toolkit tk = Toolkit.getDefaultToolkit();
	private static int xSize = ((int) tk.getScreenSize().getWidth());
	private static int ySize = ((int) tk.getScreenSize().getHeight());

	public MainView(Controller parent) {
		super("Magic Realm");
		this.addWindowListener(this);
		this.parent = parent;
	}

	@Override
	public void enterMainMenu() {
		System.out.println("main");
		MainMenu mainMenu = new MainMenu();
		setContentPane(mainMenu);
		pack();
		setVisible(true);
	}

	public void enterSplashScreen() {

		final java.awt.SplashScreen splash = java.awt.SplashScreen
				.getSplashScreen();
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
		float endTime = Timing.getSeconds() + GraphicsConfiguration.SPLASH_TIME;
		for (int i = 0; Timing.getSeconds() < endTime; i++) {
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
		parent.onSplashScreenEnd();
	}

	static void renderSplashFrame(Graphics2D g, int frame) {
		final String[] comps = { "objects", "map", "tiles", "characters",
				"environment", "view", "handlers" };
		g.setComposite(AlphaComposite.Clear);
		g.fillRect(0, 0, xSize, ySize);
		g.setPaintMode();
		g.setColor(Color.WHITE);
		g.setFont(new Font("Algerian", Font.PLAIN, 32));
		g.drawString("Loading " + comps[(frame / 5) % 7] + "...",
				(xSize / 2) - 300, ySize / 2);
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		String ObjButtons[] = { "Yes", "No" };

		int PromptResult = JOptionPane.showOptionDialog(null,
				"Are you sure you want to exit?", "Magic Realm",
				JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null,
				ObjButtons, ObjButtons[1]);
		if (PromptResult == JOptionPane.YES_OPTION) {
			parent.exit();
		}
	}

	
	
	
	
	
	// don't worry about these.
	@Override
	public void windowClosed(WindowEvent ae) {
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

}
