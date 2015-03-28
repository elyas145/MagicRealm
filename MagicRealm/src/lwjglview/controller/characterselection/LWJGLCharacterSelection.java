package lwjglview.controller.characterselection;

import java.util.ArrayList;
import java.util.List;

import config.GraphicsConfiguration;
import utils.math.linear.Matrix;
import utils.resources.ResourceHandler;
import lwjglview.graphics.LWJGLGraphics;
import lwjglview.menus.LWJGLContentPane;
import lwjglview.menus.LWJGLCounterView;
import lwjglview.menus.LWJGLPanel;
import lwjglview.selection.SelectionFrame;
import model.enums.CharacterType;

public class LWJGLCharacterSelection extends LWJGLContentPane {

	public LWJGLCharacterSelection(ResourceHandler rh, LWJGLGraphics gfx, LWJGLContentPane par) {
		super(par);
		parent = par;
		characterViews = new ArrayList<LWJGLPanel>();
		float f = -1f;
		for(CharacterType ct: CharacterType.values()) {
			LWJGLPanel cv = new LWJGLCounterView(ct.toCounter(), rh, gfx).getPanel(par, f, 0f, .5f, false);
			f += .7f;
			characterViews.add(cv);
			cv.setVisible(true);
		}
		position = Matrix.columnVector(0f, 1f, 0f);
		visible = true;
		setVisible(false);
	}
	
	public void setVisible(boolean vis) {
		if(vis ^ visible) {
			if(vis) {
				for(LWJGLPanel pane: characterViews) {
					pane.resetPosition();
				}
			}
			else {
				for(LWJGLPanel pane: characterViews) {
					pane.slide(position, GraphicsConfiguration.PANEL_TIME);
				}
			}
		}
	}

	@Override
	public void draw(LWJGLGraphics gfx) {
		for(LWJGLPanel cv: characterViews) {
			cv.draw(gfx);
		}
	}

	@Override
	public void add(LWJGLContentPane pane) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void remove(LWJGLContentPane pane) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public SelectionFrame getSelectionFrame() {
		return parent.getSelectionFrame();
	}

	@Override
	public void updateNodeUniforms(LWJGLGraphics gfx) {
	}
	
	private boolean visible;
	private List<LWJGLPanel> characterViews;
	private LWJGLContentPane parent;
	private Matrix position;

}
