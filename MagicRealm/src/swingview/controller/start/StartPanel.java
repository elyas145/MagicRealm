package swingview.controller.start;

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
import javax.swing.JPanel;
import javax.swing.border.Border;

import utils.resources.ResourceHandler;
import utils.time.Timing;
import view.controller.splashscreen.SplashScreenFinishedListener;
import config.GameConfiguration;
import config.GraphicsConfiguration;
import controller.ControllerMain;

@SuppressWarnings("serial")
public class StartPanel extends JPanel implements
		view.controller.splashscreen.SplashScreen {

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

/*	public StartPanel() {

		// init variables

		
		 * setBackground(Color.BLACK); // character selection.
		 * 
		 * // title JLabel title = new JLabel("Select Characters!");
		 * title.setForeground(Color.WHITE); title.setFont(new Font("Castellar",
		 * Font.PLAIN, 32)); title.setAlignmentX(Component.CENTER_ALIGNMENT);
		 * add(title); add(Box.createVerticalGlue());
		 * 
		 * // character cards. // labels with pics in them. once clicked, pic
		 * will change to the // reverse side of the card. for (int i = 0; i <
		 * GameConfiguration.MAX_PLAYERS; i++) { charCards[i] = new JLabel();
		 * try { charCards[i].setIcon(new ImageIcon(parent.getRh().getResource(
		 * ResourceHandler.joinPath("images", "cards",
		 * parent.getAllCharacters().get(i).getType().name()+"_F.jpg"))));
		 * charCards[i].setAlignmentY(TOP_ALIGNMENT);
		 * charCards[i].setAlignmentY(LEFT_ALIGNMENT);
		 * charCards[i].addMouseListener(this); add(charCards[i]); } catch
		 * (IOException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } }
		 

	}*/

	public void startScreen() {
		final SplashScreen splash = SplashScreen.getSplashScreen();
		if (splash == null) {
			System.out.println("SplashScreen.getSplashScreen() returned null");
			onFinish.onSplashScreenFinish();
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
		if (onFinish == null) {
			throw new RuntimeException(
					"onFinish is null, make sure to call whenFinished");
		}
		onFinish.onSplashScreenFinish();
	}

	private static Toolkit tk = Toolkit.getDefaultToolkit();
	private static int xSize = ((int) tk.getScreenSize().getWidth());
	private static int ySize = ((int) tk.getScreenSize().getHeight());
	private JLabel[] charCards;
	private SplashScreenFinishedListener onFinish;

	@Override
	public void whenFinished(SplashScreenFinishedListener listner) {
		onFinish = listner;

	}
}
