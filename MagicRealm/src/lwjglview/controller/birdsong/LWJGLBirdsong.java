package lwjglview.controller.birdsong;

import java.util.ArrayList;
import java.util.List;

import config.GraphicsConfiguration;
import lwjglview.graphics.LWJGLTextureLoader;
import lwjglview.menus.LWJGLMenuLayer;
import lwjglview.menus.LWJGLPanel;
import lwjglview.menus.dropdown.LWJGLDropdown;
import model.player.PersonalHistory;
import utils.math.linear.Matrix;
import utils.resources.ResourceHandler;
import view.controller.birdsong.BirdsongView;
import view.controller.birdsong.NewActivityListener;
import view.selection.CursorListener;
import view.selection.CursorSelection;

public class LWJGLBirdsong implements BirdsongView {

	public LWJGLBirdsong(ResourceHandler rh, LWJGLMenuLayer par) {
		borderPane = LWJGLPanel.fromPicture(par, rh,
				ResourceHandler.joinPath("menus", "birdsong", "panel.png"),
				-.76f, -1f, .4f, LWJGLPanel.Type.FOREGROUND, true);
		borderPane.setCursorListener(new CursorListener() {

			@Override
			public void onMove(int x, int y) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSelection(CursorSelection select, boolean down) {
				if(down) {
					setVisible(!visible);
				}
			}
			
		});
		borderPane.setVisible(true);
		vec3 = Matrix.zeroVector(3);
		phases = new ArrayList<LWJGLDropdown<String>>();
		List<String> options = new ArrayList<String>();
		options.add("None");
		options.add("Move");
		options.add("Hide");
		options.add("Search");
		float width = .2f;
		float height = .075f;
		LWJGLTextureLoader text = new LWJGLTextureLoader(rh,
				ResourceHandler.joinPath("menus", "main", "background.jpg"));
		for (int i = 0; i < 4; ++i) {
			LWJGLDropdown<String> select = new LWJGLDropdown<String>(borderPane, text, text, options,
					LWJGLDropdown.Type.UP, i * width * 1.1f + .34f, .15f, width, height);
			phases.add(select);
		}
		for (int i = 0; i < 5; ++i) {
			LWJGLDropdown<String> select = new LWJGLDropdown<String>(borderPane, text, text, options,
					LWJGLDropdown.Type.UP, i * width * 1.1f + .23f, .03f, width, height);
			phases.add(select);
		}
		visible = true;
		setVisible(false);
	}
	
	public void showPhases(int number) {
		int i = 0;
		for(LWJGLDropdown<String> pane: phases) {
			pane.setVisible(i < number);
			++i;
		}
	}
	
	public void setVisible(boolean vis) {
		if(visible) {
			vec3.set(1, 0, -.28f);
			borderPane.slide(vec3, GraphicsConfiguration.PANEL_TIME);
		}
		else {
			borderPane.resetPosition();
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

	private LWJGLPanel borderPane;
	private List<LWJGLDropdown<String>> phases;
	private Matrix vec3;
	private boolean visible;

}
