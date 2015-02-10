package utils.math;

public class Matrix {
	
	public Matrix(int r, int c) {
		rows = r;
		columns = c;
		data = new float[rows][columns];
	}
	
	public void identity() {
		for(int i = 0; i < rows; ++i) {
			for(int j = 0; j < columns; ++j) {
				data[i][j] = i == j ? 1 : 0;
			}
		}
	}
	
	public void translate(float[] vec) {
		int c = vec.length + 1;
		for(int i = 0; i < vec.length; ++i) {
			data[i][c] += vec[i];
		}
	}
	
	public float get(int r, int c) {
		return data[r][c];
	}
	
	public void set(int r, int c, float v) {
		data[r][c] = v;
	}
	
	private int rows;
	private int columns;
	private float[][] data;

}
