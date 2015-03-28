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
import model.player.PersonalHistory;
import utils.handler.Handler;
import utils.math.linear.Matrix;
import utils.resources.ResourceHandler;
import view.controller.birdsong.BirdsongView;
import view.controller.birdsong.NewActivityListener;
import view.selection.CursorListener;
import view.selection.CursorSelection;
import view.selection.PrimaryClickListener;

public class LWJGLBirdsong implements BirdsongView {

	private static final float COMBO_WIDTH = .6f;
	private static final float COMBO_HEIGHT = .18f;
	private static final float COMBO_VERTICAL_TOP = .20f;
	private static final float COMBO_VERTICAL_BOTTOM = .01f;

	private static final float COMBO_SPACE_TOP = 27f / 9f;
	private static final float COMBO_SPACE_BOTTOM = 32f / 9f;

	private static final float PANEL_WIDTH = 32f / 9f;

	public LWJGLBirdsong(ResourceHandler rh, LWJGLMenuLayer par) {
		borderPane = LWJGLPanel.fromPicture(par, rh,
				ResourceHandler.joinPath("menus", "birdsong", "bottom.png"),
				-16f / 9f, -1f, .787f, true);
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
				System.out.println("TODO ready clicked in birdsong view"); // TODO
			}
			
		});
		LWJGLPanel txt = LWJGLPanel.fromString(readyButton, "Ready", new Font(
				"Times New Roman", Font.BOLD, 100), Color.WHITE, 345, 120, .03f,
				s * 0.035f, .1f, false);
		txt.setVisible(true);
		readyButton.add(txt);
		readyButton.setVisible(true);
		borderPane.add(readyButton);
		phases = new ArrayList<LWJGLDropdown<String>>();
		List<String> options = new ArrayList<String>();
		options.add("None");
		options.add("Move");
		options.add("Hide");
		options.add("Search");
		float width = COMBO_WIDTH;
		float height = COMBO_HEIGHT;
		LWJGLTextureLoader stat, dyn;
		stat = new LWJGLSingleTextureLoader(rh, ResourceHandler.joinPath(
				"menus", "birdsong", "comboStatic.png"));
		dyn = new LWJGLSingleTextureLoader(rh, ResourceHandler.joinPath(
				"menus", "birdsong", "comboCell.png"));
		for (int i = 0; i < 9; ++i) {
			LWJGLDropdown<String> select = new LWJGLDropdown<String>(
					borderPane, stat, dyn, options, LWJGLDropdown.Type.UP, 0f,
					0f, width, height);
			select.setSelectionListener(new Handler<String>() {

				@Override
				public void handle(String select) {
					System.out.println("TODO selected " + select); // TODO
				}
				
			});
			borderPane.add(select);
			phases.add(select);
		}
		visible = true;
		setVisible(false);
		showPhases(7);
	}

	public void showPhases(int number) {
		resize(number);
		int i = 0;
		for (LWJGLDropdown<String> pane : phases) {
			pane.setVisible(i < number);
			++i;
		}
	}

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
	public void onNewActivity(NewActivityListener nal) {
		// TODO Auto-generated method stub

	}

	private void resize(int n) {
		int k = n / 2 + 1;
		int i = 0;
		for (int j = 1; j < k; ++j) {
			float f = (PANEL_WIDTH - COMBO_SPACE_TOP) * .5f;
			f += COMBO_SPACE_TOP * j / k - COMBO_WIDTH * .5f;
			vec3.fill(f, COMBO_VERTICAL_TOP, 0f);
			phases.get(i++).moveTo(vec3, 0f);
		}
		k = n - n / 2 + 1;
		for (int j = 1; j < k; ++j) {
			float f = (PANEL_WIDTH - COMBO_SPACE_BOTTOM) * .5f;
			f += COMBO_SPACE_BOTTOM * j / k - COMBO_WIDTH * .5f;
			vec3.fill(f, COMBO_VERTICAL_BOTTOM, 0f);
			phases.get(i++).moveTo(vec3, 0f);
		}
	}

	private LWJGLPanel borderPane;
	private LWJGLPanel readyButton;
	private List<LWJGLDropdown<String>> phases;
	private Matrix vec3;
	private boolean visible;

}
