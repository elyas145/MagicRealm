package swingview;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import client.ClientController;
import client.ControllerMain;
import lwjglview.graphics.LWJGLGraphics;
import lwjglview.graphics.board.LWJGLBoardDrawable;
import lwjglview.selection.SelectionFrame;
import model.activity.Activity;
import model.character.Phase;
import model.enums.CharacterType;
import model.enums.TileName;
import model.player.PersonalHistory;
import swingview.controller.birdsong.BirdSongView;
import swingview.controller.mainmenu.MainMenu;
import swingview.controller.search.SearchView;
import utils.resources.ResourceHandler;
import utils.time.Timing;
import view.controller.ViewController;
import config.GraphicsConfiguration;
import config.NetworkConfiguration;

@SuppressWarnings("serial")
public class MainView extends JFrame implements ViewController,
		Runnable {
	private ClientController gameController;
	
	private LWJGLBoardDrawable board;
	private LWJGLGraphics gfx;
	private ResourceHandler rh;
	private SelectionFrame sf;

	private static Toolkit tk = Toolkit.getDefaultToolkit();
	private static int xSize = ((int) tk.getScreenSize().getWidth());
	private static int ySize = ((int) tk.getScreenSize().getHeight());

	public MainView(ClientController controller) {
		super("Magic Realm");
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setPreferredSize(new Dimension(
				GraphicsConfiguration.INITIAL_MAIN_WIDTH,
				GraphicsConfiguration.INITIAL_MAIN_HEIGHT));
		setLocationRelativeTo(null);
		setLocation(xSize / 2 + GraphicsConfiguration.INITIAL_WINDOW_WIDTH / 2,
				ySize / 2 - GraphicsConfiguration.INITIAL_MAIN_HEIGHT / 2);
		this.addWindowListener(new WindowListener() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				exit();
			}
			@Override
			public void windowClosed(WindowEvent ae) {
			}
			@Override
			public void windowActivated(WindowEvent arg0) {
			}
			@Override
			public void windowDeactivated(WindowEvent arg0) {
			}
			@Override
			public void windowDeiconified(WindowEvent arg0) {
			}
			@Override
			public void windowIconified(WindowEvent arg0) {
			}
			@Override
			public void windowOpened(WindowEvent arg0) {
			}
		});
		this.gameController = controller;
		pack();
	}

	@Override
	public void run() {
	}

	@Override
	public void enterMainMenu() {
		MainMenu mainMenu = new MainMenu(this);
		setContentPane(mainMenu);
		pack();
		setVisible(true);
	}

	public void enterBirdSong(CharacterType player, int day, List<Phase> phases,
			PersonalHistory hist, Map<TileName, List<Integer>> tileClrs) {
		setPreferredSize(new Dimension(
				GraphicsConfiguration.INITIAL_BIRD_WIDTH,
				GraphicsConfiguration.INITIAL_BIRD_HEIGHT));
		BirdSongView birdSong = new BirdSongView(this, player, day, phases, hist,
				tileClrs);
		setContentPane(birdSong);
		pack();
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
		// TODO parent.onSplashScreenEnd();
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

	public void displayMessage(String string) {
		JOptionPane.showMessageDialog(this, string);
	}

	@Override
	public view.controller.search.SearchView enterSearchView(
			CharacterType character) {
		SearchView search = new SearchView(this, character);
		setContentPane(search);
		pack();
		return search;
	}

	@Override
	public void startNetworkGame() {
		try {
			gameController.connect(NetworkConfiguration.DEFAULT_IP, NetworkConfiguration.DEFAULT_PORT);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void enterLobby() {
		rh = new ResourceHandler();
		gfx = new LWJGLGraphics(rh);
		sf = new SelectionFrame(gfx);
		try {
			board = new LWJGLBoardDrawable(rh, gfx, sf);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		gfx.start();
	}

	@Override
	public void exit() {
		gameController.exit();
	}

	@Override
	public void focusOnBoard(TileName selectedTile) {
		board.focusOn(selectedTile);
	}

	@Override
	public void focusOnBoard(TileName selectedTile, Integer selectedClearing) {
		board.focusOn(selectedTile, selectedClearing);
	}

	@Override
	public void setPlayerActivities(CharacterType character,
			ArrayList<Activity> activities) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPlayerActivities(CharacterType character,
			List<Activity> activities) {
		// TODO Auto-generated method stub
		
	}

}
