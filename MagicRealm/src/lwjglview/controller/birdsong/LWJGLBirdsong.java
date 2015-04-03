package lwjglview.controller.birdsong;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import config.GraphicsConfiguration;
import lwjglview.graphics.textures.LWJGLSingleTextureLoader;
import lwjglview.graphics.textures.LWJGLTextureLoader;
import lwjglview.menus.LWJGLMenuLayer;
import lwjglview.menus.LWJGLPanel;
import lwjglview.menus.dropdown.LWJGLDropdown;
import model.character.Phase;
import model.enums.ActivityType;
import model.player.PersonalHistory;
import utils.handler.Handler;
import utils.math.linear.Matrix;
import utils.resources.ResourceHandler;
import view.controller.birdsong.ActivitiesListener;
import view.controller.birdsong.BirdsongView;
import view.selection.PrimaryClickListener;

public class LWJGLBirdsong implements BirdsongView {

	private static final int MAX_PHASES = 9;

	private static final float COMBO_WIDTH = .6f;
	private static final float COMBO_HEIGHT = .18f;
	private static final float COMBO_VERTICAL_TOP = .20f;
	private static final float COMBO_VERTICAL_BOTTOM = .01f;

	private static final float COMBO_SPACE_TOP = 27f / 9f;
	private static final float COMBO_SPACE_BOTTOM = 32f / 9f;

	private static final float PANEL_WIDTH = 32f / 9f;

	private static final Font PHASE_FONT = new Font("Times New Roman",
			Font.PLAIN, 100);
	
	private static final Color PHASE_COLOR = Color.PINK;

	public LWJGLBirdsong(ResourceHandler rh, LWJGLMenuLayer par) {
		borderPane = LWJGLPanel.fromPicture(par, rh,
				ResourceHandler.joinPath("menus", "birdsong", "bottom.png"),
				-16f / 9f, -1f, .787f, false);
		par.add(borderPane);
		borderPane.setVisible(true);
		vec3 = Matrix.zeroVector(3);
		float s = 1f;
		readyButton = LWJGLPanel.fromPicture(borderPane, rh,
				ResourceHandler.joinPath("menus", "main", "button.png"), 1.58f,
				.41f, s * .17f, true);
		readyButton.setCursorListener(new PrimaryClickListener() {

			@Override
			public void onClick() {
				readyButton.setVisible(false);
				List<ActivityType> acts = new ArrayList<ActivityType>();
				for (int i = 0; i < numPhases; ++i) {
					ActivityType add = selections[i];
					if (add == null) {
						add = ActivityType.NONE;
					}
					acts.add(add);
				}
				madeChoice.onActivitiesChosen(acts);
				setVisible(false);
			}

		});
		LWJGLPanel txt = LWJGLPanel.fromString(readyButton, "Ready", new Font(
				"Times New Roman", Font.BOLD, 100), Color.WHITE, 345, 120,
				.03f, s * 0.035f, .1f, false);
		txt.setVisible(true);
		readyButton.add(txt);
		readyButton.setVisible(true);
		borderPane.add(readyButton);
		phases = new ArrayList<Phaser>();
		List<ActivityType> options = new ArrayList<ActivityType>();
		for (ActivityType at : ActivityType.values()) {
			options.add(at);
		}
		LWJGLTextureLoader stat, dyn;
		stat = new LWJGLSingleTextureLoader(rh, ResourceHandler.joinPath(
				"menus", "birdsong", "comboStatic.png"));
		dyn = new LWJGLSingleTextureLoader(rh, ResourceHandler.joinPath(
				"menus", "birdsong", "comboCell.png"));
		for (int i = 0; i < MAX_PHASES; ++i) {
			final Phaser phr = new Phaser(stat, dyn, options);
			final LWJGLDropdown<ActivityType> select = phr.drop;
			final int j = i;
			select.setSelectionListener(new Handler<ActivityType>() {

				@Override
				public void handle(ActivityType select) {
					phr.txt.setVisible(false);
					synchronized (selections) {
						selections[j] = select;
					}
				}

			});
			borderPane.add(select);
			phases.add(phr);
		}
		numPhases = 0;
		selections = new ActivityType[MAX_PHASES];
		visible = true;
		setVisible(false);
	}

	public void showPhases(List<Phase> phss) {
		int i = 0, n = phss.size();
		numPhases = n;
		resize();
		for (Phaser ph : phases) {
			LWJGLDropdown<ActivityType> pane = ph.drop;
			selections[i] = null;
			if (i < n) {
				pane.setVisible(true);
				pane.disableAll();
				Phase phs = phss.get(i);
				pane.enableAll(phs.getPossibleActivities());
				ph.txt.updateFromString((i + 1) + ": "
						+ phss.get(i).getType().toString(), PHASE_FONT,
						PHASE_COLOR);
				ph.txt.setVisible(true);
			} else {
				pane.setVisible(false);
			}
			++i;
		}
		readyButton.setVisible(true);
		setVisible(true);
	}

	@Override
	public void setVisible(boolean vis) {
		if (visible ^ vis) {
			if (visible) {
				vec3.fill(0f, -.8f, 0f);
				borderPane.slide(vec3, GraphicsConfiguration.PANEL_TIME);
			} else {
				borderPane.resetPosition();
			}
		}
		visible = vis;
	}

	@Override
	public void updateHistory(PersonalHistory hist) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setActivitiesListener(ActivitiesListener nal) {
		madeChoice = nal;
	}

	private void resize() {
		int n = numPhases;
		int k = n / 2 + 1;
		int i = 0;
		for (int j = 1; j < k; ++j) {
			float f = (PANEL_WIDTH - COMBO_SPACE_TOP) * .5f;
			f += COMBO_SPACE_TOP * j / k - COMBO_WIDTH * .5f;
			vec3.fill(f, COMBO_VERTICAL_TOP, 0f);
			phases.get(i++).drop.moveTo(vec3, 0f);
		}
		k = n - n / 2 + 1;
		for (int j = 1; j < k; ++j) {
			float f = (PANEL_WIDTH - COMBO_SPACE_BOTTOM) * .5f;
			f += COMBO_SPACE_BOTTOM * j / k - COMBO_WIDTH * .5f;
			vec3.fill(f, COMBO_VERTICAL_BOTTOM, 0f);
			phases.get(i++).drop.moveTo(vec3, 0f);
		}
	}

	private class Phaser {

		public Phaser(LWJGLTextureLoader stat, LWJGLTextureLoader dyn,
				List<ActivityType> opts) {
			drop = new LWJGLDropdown<ActivityType>(borderPane, stat, dyn, opts,
					LWJGLDropdown.Type.UP, 0f, 0f, COMBO_WIDTH, COMBO_HEIGHT);
			txt = LWJGLPanel.fromString(drop, "Phase", PHASE_FONT, PHASE_COLOR,
					800, 120, 0f, COMBO_HEIGHT * .25f, COMBO_HEIGHT * .5f, false);
			txt.setVisible(true);
			drop.add(txt);
			borderPane.add(drop);
		}

		public LWJGLPanel txt;
		public LWJGLDropdown<ActivityType> drop;

	}

	private ActivitiesListener madeChoice;

	private LWJGLPanel borderPane;
	private LWJGLPanel readyButton;
	private List<Phaser> phases;
	
	private Matrix vec3;
	private boolean visible;

	private int numPhases;
	private ActivityType[] selections;

}
