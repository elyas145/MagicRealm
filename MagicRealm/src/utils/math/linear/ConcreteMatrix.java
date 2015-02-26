package utils.math.linear;

import java.nio.FloatBuffer;

public class ConcreteMatrix extends Matrix {
	
	private static int NUMBER_OF_MATS = 0;

	public ConcreteMatrix(int r, int c) {
		super(r, c);
		data = new float[rowCount()][columnCount()];
		++NUMBER_OF_MATS;
		System.out.println(NUMBER_OF_MATS);
	}

	public ConcreteMatrix(int r, int c, FloatBuffer fb) {
		super(r, c);
		data = new float[rowCount()][columnCount()];
		fill(fb);
		++NUMBER_OF_MATS;
		System.out.println(NUMBER_OF_MATS);
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
}
