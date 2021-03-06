package server;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import model.enums.LandType;
import model.enums.MapChitType;
import model.enums.TileName;
import config.GameConfiguration;
import config.GraphicsConfiguration;

public class CheatView extends JFrame implements ActionListener {
	private static final long serialVersionUID = -6451835899203546430L;
	private Server server;
	private JButton setTreasure;
	private JButton setSound;
	private JButton setWarning;
	private JButton start;
	private JButton ok;
	private JButton setLost;
	private Container treasurePane;
	private Container soundPane;
	private Container warningPane;
	private Container comboContainer;
	private Container defaultPane;
	private Container lostPane;
	private Container lostAndTilePane;
	private Container soundAndSitePane;
	private JButton startRandom;
	private Toolkit tk = Toolkit.getDefaultToolkit();
	private int xSize = ((int) tk.getScreenSize().getWidth());
	private int ySize = ((int) tk.getScreenSize().getHeight());

	private JComboBox<MapChitType> comboSites;
	private JComboBox<TileName> comboTiles;
	private JComboBox<MapChitType> comboSounds;
	private JComboBox<MapChitType> comboWarnings;
	private JComboBox<Integer> comboClearings;
	private JComboBox<MapChitType> comboLost;
	@SuppressWarnings("unchecked")
	private JComboBox<MapChitType> comboSiteAndSound[] = new JComboBox[5];

	public CheatView(Server ser) {
		super("Cheat View");
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setPreferredSize(new Dimension(400, 200));
		setLocationRelativeTo(null);
		setLocation(xSize / 2 + GraphicsConfiguration.INITIAL_WINDOW_WIDTH / 2,
				ySize / 2 - GraphicsConfiguration.INITIAL_MAIN_HEIGHT / 2);

		defaultPane = new Container();
		server = ser;
		defaultPane.setLayout(new BoxLayout(defaultPane, BoxLayout.Y_AXIS));
		defaultPane.add(new JLabel("CHEAT MODE"));
		setTreasure = new JButton("Set Treasure Site");
		setSound = new JButton("Set Sound");
		setWarning = new JButton("Set Warning");
		start = new JButton("Start");
		ok = new JButton("OK");
		setLost = new JButton("lost site");
		startRandom = new JButton("random chits");
		comboSites = new JComboBox<MapChitType>(MapChitType.SITES);
		comboTiles = new JComboBox<TileName>(TileName.values());
		comboTiles.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setClearings();
			}
		});
		setTreasure.addActionListener(this);
		setSound.addActionListener(this);
		setWarning.addActionListener(this);
		start.addActionListener(this);
		ok.addActionListener(this);
		setLost.addActionListener(this);
		startRandom.addActionListener(this);
		defaultPane.add(setTreasure);
		defaultPane.add(setSound);
		defaultPane.add(setWarning);
		defaultPane.add(setLost);
		defaultPane.add(startRandom);
		defaultPane.add(start);
		comboContainer = new Container();
		comboContainer
				.setLayout(new BoxLayout(comboContainer, BoxLayout.X_AXIS));
		treasurePane = new Container();
		treasurePane.setLayout(new BoxLayout(treasurePane, BoxLayout.Y_AXIS));

		comboSounds = new JComboBox<MapChitType>(MapChitType.SOUNDS);
		comboWarnings = new JComboBox<MapChitType>(MapChitType.WARNINGS);
		comboClearings = new JComboBox<Integer>();
		comboLost = new JComboBox<MapChitType>(MapChitType.LOST);

		soundPane = new Container();
		soundPane.setLayout(new BoxLayout(soundPane, BoxLayout.Y_AXIS));

		warningPane = new Container();
		warningPane.add(comboContainer);
		warningPane.add(ok);
		setClearings();
		setContentPane(defaultPane);

		lostPane = new Container();
		lostPane.setLayout(new BoxLayout(lostPane, BoxLayout.X_AXIS));
		lostAndTilePane = new Container();
		lostAndTilePane.setLayout(new BoxLayout(lostAndTilePane,
				BoxLayout.Y_AXIS));
		soundAndSitePane = new Container();
		soundAndSitePane.setLayout(new BoxLayout(soundAndSitePane,
				BoxLayout.Y_AXIS));

		for (int i = 0; i < 5; i++) {
			comboSiteAndSound[i] = new JComboBox<MapChitType>(
					MapChitType.SITE_AND_SOUND);
			soundAndSitePane.add(comboSiteAndSound[i]);
		}
		comboLost.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (((MapChitType) comboLost.getSelectedItem()) == MapChitType.LOST_CASTLE) {
					comboTiles.setModel(new JComboBox<TileName>(
							TileName.MOUNTAIN_TILES).getModel());
				} else {
					comboTiles.setModel(new JComboBox<TileName>(
							TileName.CAVE_TILES).getModel());
				}
			}
		});
		lostAndTilePane.add(comboLost);
		lostAndTilePane.add(comboTiles);
		lostPane.add(lostAndTilePane);

		lostPane.add(soundAndSitePane);
		lostPane.add(ok);
		pack();
		setVisible(true);
	}

	private void setClearings() {
		ArrayList<Integer> list = new ArrayList<Integer>();
		switch (((TileName) comboTiles.getSelectedItem()).getType()) {
		case CAVE:
			for (int i = 0; i < 6; i++) {
				list.add(i + 1);
			}
			break;
		case VALLEY:
			for (int i = 0; i < 5; i++) {
				if (i != 2)
					list.add(i + 1);
			}
			break;
		case MOUNTAIN:
		case WOODS:
			for (int i = 0; i < 6; i++) {
				list.add(i + 1);
			}
			break;
		default:
			break;
		}
		Integer arr[] = new Integer[list.size()];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = list.get(i);
		}
		comboClearings.setModel((new JComboBox<Integer>(arr)).getModel());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(setTreasure)) {
			// init and start the treasure pane.
			treasurePane.removeAll();
			comboContainer.removeAll();
			comboContainer.add(comboSites);
			comboContainer.add(comboTiles);
			treasurePane.add(comboContainer);
			treasurePane.add(ok);
			setContentPane(treasurePane);
			pack();
		} else if (e.getSource().equals(setSound)) {
			soundPane.removeAll();
			comboContainer.removeAll();
			comboContainer.add(comboSounds);
			comboContainer.add(comboTiles);
			soundPane.add(comboContainer);
			soundPane.add(ok);
			setContentPane(soundPane);
			pack();
		} else if (e.getSource().equals(setWarning)) {
			warningPane.removeAll();
			comboContainer.removeAll();
			comboContainer.add(comboWarnings);
			setWarningTiles();
			comboContainer.add(comboTiles);
			warningPane.add(comboContainer);
			warningPane.add(ok);
			setContentPane(warningPane);
			pack();
		} else if (e.getSource().equals(ok)) {
			// check which pane is set.
			if (getContentPane().equals(treasurePane)) {
				// add the treasure to the model.
				server.addSite((MapChitType) comboSites.getSelectedItem(),
						(TileName) comboTiles.getSelectedItem());

			} else if (getContentPane().equals(soundPane)) {
				server.addSound((MapChitType) comboSounds.getSelectedItem(),
						(TileName) comboTiles.getSelectedItem());
			} else if (getContentPane().equals(warningPane)) {
				server.addWarning(
						(MapChitType) comboWarnings.getSelectedItem(),
						(TileName) comboTiles.getSelectedItem());
				resetComboTiles();

			} else if (getContentPane().equals(lostPane)) {
				ArrayList<MapChitType> soundAndSiteArray = new ArrayList<MapChitType>();
				for (int i = 0; i < comboSiteAndSound.length; i++) {
					soundAndSiteArray.add((MapChitType) comboSiteAndSound[i]
							.getSelectedItem());
				}
				switch ((MapChitType) comboLost.getSelectedItem()) {
				case LOST_CITY:
					server.setLost(MapChitType.LOST_CITY, soundAndSiteArray,
							(TileName) comboTiles.getSelectedItem());
					break;
				case LOST_CASTLE:
					server.setLost(MapChitType.LOST_CASTLE, soundAndSiteArray,
							(TileName) comboTiles.getSelectedItem());
					break;
				default:
					break;
				}
			}
			setContentPane(defaultPane);
			pack();
		} else if (e.getSource().equals(start)) {
			server.doneSettingCheatMode();
			JOptionPane
					.showMessageDialog(
							this,
							"This window will now close. the only things setup are those you have set manually. nothing else is set.");
			this.setVisible(false);

		} else if (e.getSource().equals(setLost)) {
			setContentPane(lostPane);
			comboTiles
					.setModel(new JComboBox<TileName>(TileName.MOUNTAIN_TILES)
							.getModel());
			pack();
		}else if(e.getSource().equals(startRandom)){
			server.doneSettingCheatMode();
			GameConfiguration.RANDOM = true;
			JOptionPane
					.showMessageDialog(
							this,
							"This window will now close. chits will be set according to algorithm.");
			this.setVisible(false);
		}
	}

	private void resetComboTiles() {
		comboTiles.setModel(new JComboBox<TileName>(TileName.values())
				.getModel());

	}

	private void setWarningTiles() {
		ArrayList<TileName> list = new ArrayList<TileName>();
		for (TileName n : TileName.values()) {
			if (n.getType() != LandType.VALLEY) {
				list.add(n);
			}
		}
		TileName arr[] = new TileName[list.size()];
		for (int i = 0; i < arr.length; ++i) {
			arr[i] = list.get(i);
		}
		comboTiles.setModel(new JComboBox<TileName>(arr).getModel());

	}
}
