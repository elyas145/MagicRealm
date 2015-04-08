package utils.math.linear;

public class SubMatrix extends Matrix {

	public SubMatrix(Matrix par, int i, int j) {
		super(par.rowCount() - 1, par.columnCount() - 1);
		row = i;
		column = j;
		parent = par;
	}

	@Override
	public float get(int r, int c) {
		return parent.get(r < row ? r : r + 1, c < column ? c : c + 1);
	}

	@Override
	public void set(int r, int c, float v) {
		parent.set(r < row ? r : r + 1, c < column ? c : c + 1, v);
	}
	
	@Override
	protected int getChange() {
		return parent.getChange();
	}
	
	@Override
	public String toString() {
		return "Submatrix: " + super.toString();
	}

	private int row;
	private int column;
	private Matrix parent;
	
	private static final long serialVersionUID = 3038551738368620099L;
	
}
