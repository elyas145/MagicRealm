package lwjglview.menus.characterselection;

import java.util.ArrayList;
import java.util.List;

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
	
	private List<LWJGLPanel> characterViews;
	private LWJGLContentPane parent;

}
