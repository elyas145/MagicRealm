package lwjglview.menus;

import lwjglview.graphics.LWJGLDrawableNode;
import lwjglview.graphics.animator.matrixcalculator.MatrixCalculator;
import lwjglview.selection.SelectionFrame;

public abstract class LWJGLContentPane extends LWJGLDrawableNode {
	
	protected LWJGLContentPane(LWJGLDrawableNode par, MatrixCalculator mat) {
		super(par, mat);
	}
	
	protected LWJGLContentPane(LWJGLDrawableNode par) {
		super(par);
	}

	public abstract void add(LWJGLContentPane pane);

	public abstract void remove(LWJGLContentPane pane);
	
	public abstract SelectionFrame getSelectionFrame();

}
