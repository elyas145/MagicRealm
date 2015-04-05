package lwjglview.menus;

import lwjglview.graphics.LWJGLDrawableNode;
import lwjglview.graphics.animator.matrixcalculator.MatrixCalculator;
import lwjglview.selection.SelectionFrame;

public abstract class LWJGLContentPane extends LWJGLDrawableNode {
	
	protected LWJGLContentPane(LWJGLDrawableNode par, MatrixCalculator mat) {
		super(par, mat);
		parent = null;
	}
	
	protected LWJGLContentPane(LWJGLContentPane par) {
		super(par);
	}
	
	public SelectionFrame getSelectionFrame() {
		return getParent().getSelectionFrame();
	}
	
	public LWJGLContentPane getParent() {
		return parent;
	}

	public void add(LWJGLContentPane pane) {}

	public void remove(LWJGLContentPane pane) {}
	
	private LWJGLContentPane parent;

}
