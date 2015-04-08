package utils.math.linear;

import java.nio.FloatBuffer;

public class ConcreteMatrix extends Matrix {

	public ConcreteMatrix(int r, int c) {
		super(r, c);
		data = new float[rowCount()][columnCount()];
	}

	public ConcreteMatrix(int r, int c, FloatBuffer fb) {
		super(r, c);
		data = new float[rowCount()][columnCount()];
		fill(fb);
	}

	@Override
	public float get(int r, int c) {
		return data[r][c];
	}

	@Override
	public void set(int r, int c, float v) {
		data[r][c] = v;
		change();
	}

	private float[][] data;

	private static final long serialVersionUID = -1097899023896491157L;

}
