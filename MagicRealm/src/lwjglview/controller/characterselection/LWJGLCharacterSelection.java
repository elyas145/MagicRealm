package lwjglview.controller.characterselection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import config.GraphicsConfiguration;
import utils.math.linear.Matrix;
import utils.resources.ResourceHandler;
import view.controller.characterselection.CharacterSelectionListener;
import view.controller.characterselection.CharacterSelectionView;
import view.selection.PrimaryClickListener;
import lwjglview.graphics.LWJGLGraphics;
import lwjglview.menus.LWJGLContentPane;
import lwjglview.menus.LWJGLCounterView;
import lwjglview.menus.LWJGLPanel;
import lwjglview.selection.SelectionFrame;
import model.enums.CharacterType;

public class LWJGLCharacterSelection extends LWJGLContentPane implements
		CharacterSelectionView {

	public LWJGLCharacterSelection(ResourceHandler rh, LWJGLGraphics gfx,
			LWJGLContentPane par) {
		super(par);
		parent = par;
		characterViews = new HashMap<CharacterType, LWJGLPanel>();
		float f = -1f;
		for (CharacterType ct : CharacterType.values()) {
			LWJGLPanel cv = new LWJGLCounterView(ct.toCounter(), rh, gfx)
					.getPanel(par, f, 1f, .6f, true);
			cv.setCursorListener(new CharacterClick(ct));
			f += .7f;
			characterViews.put(ct, cv);
			cv.setVisible(true);
		}
		position = Matrix.columnVector(0f, -1.3f, 0f);
		visible = false;
	}

	public void setVisible(boolean vis) {
		visible = vis;
	}

	@Override
	public void selectCharacter(List<CharacterType> characters,
			CharacterSelectionListener onselect) {
		hideAll();
		for (CharacterType ct : characters) {
			show(ct);
		}
		onSelect = onselect;
		setVisible(true);
	}

	@Override
	public void draw(LWJGLGraphics gfx) {
		if(visible) {
			for (LWJGLPanel cv : characterViews.values()) {
				cv.draw(gfx);
			}
		}
	}

	@Override
	public void add(LWJGLContentPane pane) {
	}

	@Override
	public void remove(LWJGLContentPane pane) {
	}

	@Override
	public SelectionFrame getSelectionFrame() {
		return parent.getSelectionFrame();
	}

	@Override
	public void updateNodeUniforms(LWJGLGraphics gfx) {
	}

	private void show(CharacterType ct) {
		characterViews.get(ct)
				.slide(position, GraphicsConfiguration.PANEL_TIME);
	}
	
	private void hideAll() {
		for (LWJGLPanel pane : characterViews.values()) {
			pane.resetPosition();
		}
	}

	private class CharacterClick extends PrimaryClickListener {

		public CharacterClick(CharacterType ct) {
			character = ct;
		}

		@Override
		public void onClick() {
			onSelect.onCharacterSelected(character);
		}

		private CharacterType character;

	}

	private boolean visible;
	private Map<CharacterType, LWJGLPanel> characterViews;
	private LWJGLContentPane parent;
	private Matrix position;

	private CharacterSelectionListener onSelect;

}
