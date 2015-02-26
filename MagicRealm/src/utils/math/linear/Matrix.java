package utils.math.linear;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import utils.math.Mathf;

public abstract class Matrix {

	public static void main(String[] args) {
		Matrix test = Matrix.initialize(4, 4, new float[] { -0.9623067f,
				0.19230938f, -0.19230938f, 0.2943588f, -0.27196655f,
				-0.68045354f, 0.68045354f, -4.278378f, 0.0f, 0.70710677f,
				0.70710677f, 0.65234303f, 0.0f, 0.0f, 0.0f, 1.0f });
	}

	public static Matrix clone(Matrix inp) {
		Matrix ret = Matrix.empty(inp.rowCount(), inp.columnCount());
		ret.copyFrom(inp);
		return ret;
	}

	public static Matrix rotationX(int size, float ang) {
		Matrix rot = Matrix.empty(size, size);
		rot.rotateX(ang);
		return rot;
	}

	public static Matrix rotationY(int size, float ang) {
		Matrix rot = Matrix.empty(size, size);
		rot.rotateY(ang);
		return rot;
	}

	public static Matrix rotationZ(int size, float ang) {
		Matrix rot = Matrix.empty(size, size);
		rot.rotateZ(ang);
		return rot;
	}

	public static Matrix rotation(float ang) {
		Matrix rot = Matrix.empty(2, 2);
		rot.rotate(ang);
		return rot;
	}

	public static Matrix translation(float... data) {
		int sz = data.length + 1;
		Matrix trans = Matrix.empty(sz, sz);
		trans.translate(data);
		return trans;
	}

	public static Matrix translation(Matrix mat) {
		int sz = mat.rowCount() + 1;
		Matrix trans = Matrix.empty(sz, sz);
		trans.translate(mat);
		return trans;
	}

	public static Matrix dilation(float... fs) {
		Matrix ret = Matrix.empty(fs.length, fs.length);
		ret.scale(fs);
		return ret;
	}

	public static Matrix columnVector(float... fs) {
		Matrix ret = Matrix.zeroVector(fs.length);
		for (int i = 0; i < fs.length; ++i) {
			ret.set(i, 0, fs[i]);
		}
		return ret;
	}
	
	public static Matrix zeroVector(int sz) {
		return Matrix.empty(sz, 1);
	}

	public static Matrix empty(int rows, int columns) {
		return new ConcreteMatrix(rows, columns);
	}

	public static Matrix identity(int i) {
		Matrix ret = Matrix.empty(i, i);
		ret.identity();
		return ret;
	}

	public static Matrix square(int sz) {
		return Matrix.empty(sz, sz);
	}

	public static Matrix initialize(int r, int c, FloatBuffer fb) {
		return new ConcreteMatrix(r, c, fb);
	}

	public static Matrix initialize(int r, int c, float[] fb) {
		return Matrix.initialize(r, c, FloatBuffer.wrap(fb));
	}

	public abstract float get(int i, int j);

	public abstract void set(int i, int j, float v);

	// new matrix with blank data
	public Matrix(int r, int c) {
		rows = r;
		columns = c;
		augmentChanged = -1;
		augment = null;
		detChanged = -1;
		det = 0f;
		children = null;
		changeNumber = 0;
		buffer = null;
	}

	public Matrix sub(int i, int j) {
		if (children == null) {
			children = new Matrix[rowCount()][columnCount()];
		}
		if (children[i][j] == null) {
			children[i][j] = new SubMatrix(this, i, j);
		}
		return children[i][j];
	}

	public void identity() {
		for (int i = 0; i < rows; ++i) {
			for (int j = 0; j < columns; ++j) {
				set(i, j, i == j ? 1f : 0f);
			}
		}
	}

	public void translate(float... vec) {
		identity();
		int c = (int) vec.length;
		for (int i = 0; i < vec.length; ++i) {
			set(i, c, vec[i]);
		}
	}

	public void translate(Matrix vec) {
		identity();
		int c = vec.rowCount();
		for (int i = 0; i < vec.rowCount(); ++i) {
			set(i, c, vec.get(i, 0));
		}
	}

	public void scale(float... vec) {
		for (int i = 0; i < rowCount(); ++i) {
			for (int j = 0; j < columnCount(); ++j) {
				set(i, j, i == j ? vec[i] : 0f);
			}
		}
	}

	public void rotateX(float ang) {
		float sin = (float) Math.sin(ang);
		float cos = (float) Math.cos(ang);
		identity();
		set(1, 1, cos);
		set(1, 2, -sin);
		set(2, 1, sin);
		set(2, 2, cos);
	}

	public void rotateY(float ang) {
		float sin = (float) Math.sin(ang);
		float cos = (float) Math.cos(ang);
		identity();
		set(0, 0, cos);
		set(0, 2, -sin);
		set(2, 0, sin);
		set(2, 2, cos);
	}

	public void rotateZ(float ang) {
		float sin = (float) Math.sin(ang);
		float cos = (float) Math.cos(ang);
		identity();
		set(0, 0, cos);
		set(0, 1, -sin);
		set(1, 0, sin);
		set(1, 1, cos);
	}

	public void rotate(float ang) {
		rotateZ(ang);
	}

	public void perspective(float fov, float ar, float nc, float fc) {
		float vang = fov * .5f;
		float t = nc * (float) Math.tan(vang);
		float b = -t;
		float r = ar * t;
		float l = -r;
		frustum(l, r, b, t, nc, fc);
	}

	public void frustum(float l, float r, float b, float t, float n, float f) {
		/*
		 * 2n / (r - l) 0 (r + l) / (r - l) 0 0 2n / (t - b) (t + b) / (t - b) 0
		 * 0 0 - (f + n) / (f - n) - (2fn) / (f - n) 0 0 -1 0
		 */
		identity();
		set(0, 0, 2 * n / (r - l));
		set(0, 2, (r + l) / (r - l));
		set(1, 1, 2 * n / (t - b));
		set(1, 2, (t + b) / (t - b));
		set(2, 2, -(f + n) / (f - n));
		set(2, 3, -(2 * f * n) / (f - n));
		set(3, 2, -1f);
	}

	public void add(Matrix other, Matrix dest) {
		for (int i = 0; i < dest.rowCount(); ++i) {
			for (int j = 0; j < dest.columnCount(); ++j) {
				dest.set(i, j, get(i, j) + other.get(i, j));
			}
		}
	}

	public Matrix add(Matrix other) {
		Matrix ret = Matrix.empty(rowCount(), columnCount());
		add(other, ret);
		return ret;
	}

	public void subtract(Matrix other, Matrix dest) {
		for (int i = 0; i < dest.rowCount(); ++i) {
			for (int j = 0; j < dest.columnCount(); ++j) {
				dest.set(i, j, get(i, j) - other.get(i, j));
			}
		}
	}

	public Matrix subtract(Matrix other) {
		Matrix ret = Matrix.empty(rowCount(), columnCount());
		subtract(other, ret);
		return ret;
	}

	public void multiply(float k, Matrix dest) {
		for (int i = 0; i < dest.rowCount(); ++i) {
			for (int j = 0; j < dest.columnCount(); ++j) {
				dest.set(i, j, get(i, j) * k);
			}
		}
	}

	public Matrix multiply(float k) {
		Matrix ret = Matrix.empty(rowCount(), columnCount());
		multiply(k, ret);
		return ret;
	}

	public void multiply(Matrix other, Matrix dest) {
		int mult = columnCount();
		int rc, cc;
		rc = dest.rowCount();
		cc = dest.columnCount();
		if (dest == this || other == dest) {
			if (buffer == null) {
				buffer = new float[mult];
			}
		}
		if (dest == this) {
			for (int i = 0; i < rc; ++i) {
				for (int j = 0; j < cc; ++j) {
					float sm = 0f;
					for (int k = 0; k < mult; ++k) {
						sm += get(i, k) * other.get(k, j);
					}
					buffer[j] = sm;
				}
				for (int j = 0; j < cc; ++j) {
					set(i, j, buffer[j]);
				}
			}
		} else if (other == dest) {
			for (int j = 0; j < cc; ++j) {
				for (int i = 0; i < rc; ++i) {
					float sm = 0f;
					for (int k = 0; k < mult; ++k) {
						sm += get(i, k) * other.get(k, j);
					}
					buffer[i] = sm;
				}
				for (int i = 0; i < rc; ++i) {
					other.set(i, j, buffer[i]);
				}
			}
		} else {
			for (int i = 0; i < rc; ++i) {
				for (int j = 0; j < cc; ++j) {
					float sm = 0f;
					for (int k = 0; k < mult; ++k) {
						sm += get(i, k) * other.get(k, j);
					}
					dest.set(i, j, sm);
				}
			}
		}
	}

	public Matrix multiply(Matrix other) {
		int mult = columnCount();
		if (mult != other.rowCount()) {
			throw new ArithmeticException("Multiply incompatible matrices");
		}
		Matrix ret = Matrix.empty(rowCount(), other.columnCount());
		multiply(other, ret);
		return ret;
	}

	public Matrix transpose() {
		int rc, cc;
		rc = rowCount();
		cc = columnCount();
		Matrix ret = Matrix.empty(cc, rc);
		for (int i = 0; i < rc; ++i) {
			for (int j = 0; j < cc; ++j) {
				ret.set(j, i, get(i, j));
			}
		}
		return ret;
	}

	public int rowCount() {
		return rows;
	}

	public int columnCount() {
		return columns;
	}

	public boolean isSquare() {
		return rows == columns;
	}

	public float length() {
		float v = 0f;
		for (int i = 0; i < rowCount(); ++i) {
			for (int j = 0; j < columnCount(); ++j) {
				v += Mathf.sqr(get(i, j));
			}
		}
		return Mathf.sqrt(v);
	}

	public float determinant() {
		if (!isSquare()) {
			throw new ArithmeticException("Determinant of non-square matrix");
		}
		int change = getChange();
		if (detChanged == change) {
			return det;
		}
		detChanged = change;
		switch (rowCount()) {
		case 1:
			det = 1f / get(0, 0);
			break;
		case 2:
			det = get(0, 0) * get(1, 1) - get(0, 1) * get(1, 0);
			break;
		case 3:
			det = determinant3();
			break;
		default:
			det = determinantN();
			break;
		}
		return det;
	}

	public void inverse(Matrix dest) {
		int size = rowCount();
		switch (size) {
		case 1:
			dest.set(0, 0, 1f / get(0, 0));
			return;
		case 2:
			inverse2(dest);
			return;
		case 3:
			inverse3(dest);
			return;
		case 4:
			inverse4(dest);
			return;
		default:
			break;
		}
		inverseN(dest, size);
	}

	public Matrix inverse() {
		if (!isSquare()) {
			throw new ArithmeticException("Inverse of non-square matrix");
		}
		Matrix ret = Matrix.square(rowCount());
		inverse(ret);
		return ret;
	}

	public void subtractRow(int to, int from, float scale) {
		for (int j = 0; j < columnCount(); ++j) {
			set(to, j, get(to, j) - get(from, j) * scale);
		}
	}

	public void subtractRow(int to, int from) {
		for (int j = 0; j < columnCount(); ++j) {
			set(to, j, get(to, j) - get(from, j));
		}
	}

	public void multiplyRow(int i, float k) {
		for (int j = 0; j < columnCount(); ++j) {
			set(i, j, get(i, j) * k);
		}
	}

	public void multiplyColumn(int j, float k) {
		for (int i = 0; i < rowCount(); ++i) {
			set(i, j, get(i, j) * k);
		}
	}

	public void swapRow(int i, int k) {
		for (int j = 0; j < columnCount(); ++j) {
			float tmp = get(i, j);
			set(i, j, get(k, j));
			set(k, j, tmp);
		}
	}

	public void fill(float... fb) {
		int k = 0;
		for (int i = 0; i < rowCount(); ++i) {
			for (int j = 0; j < columnCount(); ++j) {
				set(i, j, fb[k++]);
			}
		}
	}

	public void fill(FloatBuffer fb) {
		int k = 0;
		for (int i = 0; i < rowCount(); ++i) {
			for (int j = 0; j < columnCount(); ++j) {
				set(i, j, fb.get(k++));
			}
		}
	}

	public void toFloatBuffer(FloatBuffer fb) {
		fb.clear();
		for (int i = 0; i < rowCount(); ++i) {
			for (int j = 0; j < columnCount(); ++j) {
				fb.put(get(i, j));
			}
		}
		fb.compact();
	}

	public FloatBuffer toFloatBuffer() {
		FloatBuffer fb = BufferUtils.createFloatBuffer(rowCount()
				* columnCount());
		toFloatBuffer(fb);
		return fb;
	}

	public void toByteBuffer(ByteBuffer bb) {
		bb.clear();
		bb.order(ByteOrder.nativeOrder());
		int k, rc, cc;
		k = 0;
		rc = rowCount();
		cc = columnCount();
		for (int i = 0; i < rc; ++i) {
			for (int j = 0; j < cc; ++j) {
				bb.putFloat(k, get(i, j));
				k += 4;
			}
		}
		bb.flip();
	}

	public ByteBuffer toByteBuffer() {
		ByteBuffer bb = ByteBuffer.allocateDirect(rowCount() * columnCount()
				* 4);
		toByteBuffer(bb);
		return bb;
	}
	
	public void copyFrom(Matrix other) {
		copyFrom(other, 0, 0);
	}

	public void copyFrom(Matrix other, int srow, int scol) {
		int rc, cc;
		rc = rowCount();
		cc = columnCount();
		for (int i = 0; i < rc; ++i) {
			for (int j = 0; j < cc; ++j) {
				set(i, j, other.get(i + srow, j + scol));
			}
		}
	}

	@Override
	public String toString() {
		String msg = "[";
		for (int i = 0; i < rowCount(); ++i) {
			msg += "[";
			for (int j = 0; j < columnCount(); ++j) {
				msg += get(i, j);
				if (j + 1 != columnCount()) {
					msg += ", ";
				}
			}
			msg += i + 1 == rowCount() ? "]" : "], ";
		}
		return msg + "]";
	}
	
	// TODO implement equals

	/*
	 * protected abstract methods
	 */

	protected final void change() {
		++changeNumber;
	}

	protected int getChange() {
		return changeNumber;
	}

	private float determinant3() {
		float a, b, c, d, e, f, g, h, i;
		a = get(0, 0);
		b = get(0, 1);
		c = get(0, 2);
		d = get(1, 0);
		e = get(1, 1);
		f = get(1, 2);
		g = get(2, 0);
		h = get(2, 1);
		i = get(2, 2);
		return determinant(a, b, c, d, e, f, g, h, i);
	}

	private static float determinant(float a, float b, float c, float d,
			float e, float f, float g, float h, float i) {
		return a * e * i + b * f * g + c * d * h - c * e * g - a * f * h - b
				* d * i;
	}

	private float determinantN() {
		float det = 0f;
		int k = 1;
		int rc = rowCount();
		for (int i = 0; i < rc; ++i) {
			det += k * get(i, 0) * sub(i, 0).determinant();
			k *= -1;
		}
		return det;
	}

	private void inverse2(Matrix dest) {
		float a, b, c, d;
		a = get(0, 0);
		b = get(0, 1);
		c = get(1, 0);
		d = get(1, 1);
		dest.set(0, 0, d);
		dest.set(0, 1, -b);
		dest.set(1, 0, -c);
		dest.set(1, 1, a);
		dest.multiply(1f / (a * d - b * c), dest);
	}

	private void inverse3(Matrix dest) {
		float a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, z;
		a = get(0, 0);
		b = get(0, 1);
		c = get(0, 2);
		d = get(1, 0);
		e = get(1, 1);
		f = get(1, 2);
		g = get(2, 0);
		h = get(2, 1);
		i = get(2, 2);
		j = e * i - h * f;
		k = f * g - d * i;
		l = d * h - e * g;
		m = c * h - b * i;
		n = a * i - c * g;
		o = b * g - a * h;
		p = b * f - e * c;
		q = c * d - a * f;
		r = a * e - b * d;
		z = 1f / determinant(a, b, c, d, e, f, g, h, i);
		dest.set(0, 0, j * z);
		dest.set(0, 1, m * z);
		dest.set(0, 2, p * z);
		dest.set(1, 0, k * z);
		dest.set(1, 1, n * z);
		dest.set(1, 2, q * z);
		dest.set(2, 0, l * z);
		dest.set(2, 1, o * z);
		dest.set(2, 2, r * z);
	}

	private void inverse4(Matrix dest) {
		float det = 1f / determinantN();
		int ki = 1;
		for (int i = 0; i < 4; ++i) {
			int kj = ki;
			for (int j = 0; j < 4; ++j) {
				dest.set(i, j, kj * sub(j, i).determinant3() * det);
				kj *= -1;
			}
			ki *= -1;
		}
	}

	private void inverseN(Matrix dest, int size) {
		int change = getChange();
		if (augmentChanged == change) {
			copyAugment(dest, size);
			return;
		}
		// set up an augmented matrix
		initializeAugment();
		// initialize the augmented matrix
		Matrix tmp = augment;
		ArithmeticException notInvertible = new ArithmeticException(
				"Matrix not invertible");
		try {
			// Perform lower row reduction
			for (int i = 0; i < size; ++i) {
				// Find the first non-zero ith entry
				int k = i;
				while (k < size && tmp.get(k, i) == 0f) {
					++k;
				}
				if (k == size) {
					throw notInvertible;
				}
				// Swap for so first entry is non-zero
				if (k != i) {
					tmp.swapRow(i, k);
				}
				// Set first entry to 1
				tmp.multiplyRow(i, 1f / tmp.get(i, i));
				for (k = i + 1; k < size; ++k) {
					// Zero out lower column entry
					tmp.subtractRow(k, i, tmp.get(k, i));
				}
			}
		} catch (ArithmeticException ae) {
			throw notInvertible;
		}
		// Perform upper row reduction
		for (int i = size - 1; i > 0; --i) {
			for (int k = i - 1; k >= 0; --k) {
				tmp.subtractRow(k, i, tmp.get(k, i));
			}
		}
		copyAugment(dest, size);
	}

	private void copyAugment(Matrix dest, int size) {
		// Copy augmented matrix to dest
		for (int i = 0; i < size; ++i) {
			for (int j = 0; j < size; ++j) {
				dest.set(i, j, augment.get(i, j + size));
			}
		}
	}

	private void initializeAugment() {
		int size = rowCount();
		if (augment == null) {
			augment = Matrix.empty(size, size * 2);
		}
		for (int i = 0; i < size; ++i) {
			for (int j = 0; j < size; ++j) {
				augment.set(i, j, get(i, j));
				augment.set(i, j + size, i == j ? 1f : 0f);
			}
		}
	}

	private float[] buffer;
	private int changeNumber;
	private Matrix[][] children;
	private int rows;
	private int columns;
	private int augmentChanged;
	private Matrix augment;
	private int detChanged;
	private float det;
}
