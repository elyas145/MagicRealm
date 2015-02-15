package utils.math;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

public class Matrix {
	
	public static Matrix rotationX(int size, float ang) {
		Matrix rot = new Matrix(size, size);
		rot.rotateX(ang);
		return rot;
	}
	
	public static Matrix rotationY(int size, float ang) {
		Matrix rot = new Matrix(size, size);
		rot.rotateY(ang);
		return rot;
	}
	
	public static Matrix rotationZ(int size, float ang) {
		Matrix rot = new Matrix(size, size);
		rot.rotateZ(ang);
		return rot;
	}
	
	public static Matrix translation(float[] data) {
		int sz = data.length + 1;
		Matrix trans = new Matrix(sz, sz);
		trans.translate(data);
		return trans;
	}
	
	public static Matrix dilation(float[] fs) {
		Matrix ret = new Matrix(fs.length, fs.length);
		ret.scale(fs);
		return ret;
	}
	
	public static Matrix identityMatrix(int i) {
		Matrix ret = new Matrix(i, i);
		ret.identity();
		return ret;
	}
	
	// new matrix with blank data
	public Matrix(int r, int c) {
		rows = r;
		columns = c;
		data = new float[rows][columns];
	}
	
	// new matrix from FloatBuffer data
	public Matrix(int r, int c, FloatBuffer fb) {
		rows = r;
		columns = c;
		data = new float[rows][columns];
		int k = 0;
		for(int i = 0; i < rows; ++i) {
			for(int j = 0; j < columns; ++j) {
				set(i, j, fb.get(k++));
			}
		}
	}
	
	public void identity() {
		for(int i = 0; i < rows; ++i) {
			for(int j = 0; j < columns; ++j) {
				set(i, j, i == j ? 1f : 0f);
			}
		}
	}
	
	public void translate(float[] vec) {
		identity();
		int c = vec.length;
		for(int i = 0; i < vec.length; ++i) {
			set(i, c, vec[i]);
		}
	}
	
	public void scale(float[] vec) {
		for(int i = 0; i < rowCount(); ++i) {
			for(int j = 0; j < columnCount(); ++j) {
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
			2n / (r - l)	0	(r + l) / (r - l)	0
			0	2n / (t - b)	(t + b) / (t - b)	0
			0	0	- (f + n) / (f - n)	- (2fn) / (f - n)
			0	0	-1	0
		*/
		identity();
		set(0, 0, 2 * n / (r - l));
		set(0, 2, (r + l) / (r - l));
		set(1, 1, 2 * n / (t - b));
		set(1, 2, (t + b) / (t - b));
		set(2, 2, - (f + n) / (f - n));
		set(2, 3, - (2 * f * n) / (f - n));
		set(3, 2, -1f);
	}
	
	public Matrix multiply(float k) {
		Matrix ret = new Matrix(rowCount(), columnCount());
		for(int i = 0; i < ret.rowCount(); ++i) {
			for(int j = 0; j < ret.columnCount(); ++j) {
				ret.set(i, j, get(i, j) * k);
			}
		}
		return ret;
	}
	
	public Matrix multiply(Matrix other) {
		int mult = columnCount();
		if(mult != other.rowCount()) {
			throw new ArithmeticException("Multiply incompatible matrices");
		}
		Matrix ret = new Matrix(rowCount(), other.columnCount());
		for(int i = 0; i < ret.rowCount(); ++i) {
			for(int j = 0; j < ret.columnCount(); ++j) {
				float sm = 0;
				for(int k = 0; k < mult; ++k) {
					sm += get(i, k) * other.get(k, j);
				}
				ret.set(i, j, sm);
			}
		}
		return ret;
	}
	
	public Matrix transpose() {
		Matrix ret = new Matrix(columnCount(), rowCount());
		for(int i = 0; i < rowCount(); ++i) {
			for(int j = 0; j < columnCount(); ++j) {
				ret.set(i, j, get(j, i));
			}
		}
		return ret;
	}
	
	public Matrix inverse() {
		if(!isSquare()) {
			throw new ArithmeticException("Inverse of non-square matrix");
		}
		int size = rowCount();
		// set up an augmented matrix
		Matrix tmp = new Matrix(size, size * 2);
		// initialize the augmented matrix
		for(int i = 0; i < size; ++i) {
			for(int j = 0; j < size; ++j) {
				tmp.set(i, j, get(i, j));
				tmp.set(i, j + size, i == j ? 1f : 0f);
			}
		}
		ArithmeticException notInvertible = new ArithmeticException("Matrix not invertible");
		try {
			// Perform lower row reduction
			for(int i = 0; i < size; ++i) {
				// Find the first non-zero ith entry
				int k = i;
				while(k < size && tmp.get(k, i) == 0f) { ++k; }
				if(k == size) {
					throw notInvertible;
				}
				// Swap for so first entry is non-zero
				if(k != i) {
					tmp.swapRow(i, k);
				}
				// Set first entry to 1
				tmp.multiplyRow(i, 1f / tmp.get(i, i));
				for(k = i + 1; k < size; ++k) {
					// Zero out lower column entry
					tmp.subtractRow(k, i, tmp.get(k, i));
				}
			}
		}
		catch(ArithmeticException ae) {
			throw notInvertible;
		}
		// Perform upper row reduction
		for(int i = size - 1; i > 0; --i) {
			for(int k = i - 1; k >= 0; --k) {
				tmp.subtractRow(k, i, tmp.get(k, i));
			}
		}
		// Copy augmented matrix to a new matrix
		Matrix ret = new Matrix(size, size);
		for(int i = 0; i < size; ++i) {
			for(int j = 0; j < size; ++j) {
				ret.set(i, j, tmp.get(i, j + size));
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
	
	public float get(int r, int c) {
		return data[r][c];
	}
	
	public void set(int r, int c, float v) {
		data[r][c] = v;
	}
	
	public void subtractRow(int to, int from, float scale) {
		for(int j = 0; j < columnCount(); ++j) {
			set(to, j, get(to, j) - get(from, j) * scale);
		}
	}
	
	public void subtractRow(int to, int from) {
		for(int j = 0; j < columnCount(); ++j) {
			set(to, j, get(to, j) - get(from, j));
		}
	}
	
	public void multiplyRow(int i, float k) {
		for(int j = 0; j < columnCount(); ++j) {
			set(i, j, get(i, j) * k);
		}
	}
	
	public void multiplyColumn(int j, float k) {
		for(int i = 0; i < rowCount(); ++i) {
			set(i, j, get(i, j) * k);
		}
	}
	
	public void swapRow(int i, int k) {
		for(int j = 0; j < columnCount(); ++j) {
			float tmp = get(i, j);
			set(i, j, get(k, j));
			set(k, j, tmp);
		}
	}
	
	public void toFloatBuffer(FloatBuffer fb) {
		fb.clear();
		for(float[] floats: data) {
			fb.put(floats);
		}
		fb.compact();
	}
	
	public FloatBuffer toFloatBuffer() {
		FloatBuffer fb = BufferUtils.createFloatBuffer(rowCount() * columnCount());
		toFloatBuffer(fb);
		return fb;
	}
	
	public void toByteBuffer(ByteBuffer bb) {
		bb.clear();
		bb.order(ByteOrder.nativeOrder());
		int k = 0;
		for(int i = 0; i < rowCount(); ++i) {
			for(int j = 0; j < columnCount(); ++j) {
				bb.putFloat(k, get(i, j));
				k += 4;
			}
		}
		bb.flip();
	}
	
	public ByteBuffer toByteBuffer() {
		ByteBuffer bb = ByteBuffer.allocateDirect(rowCount() * columnCount() * 4);
		toByteBuffer(bb);
		return bb;
	}
	
	@Override
	public String toString() {
		String msg = "[";
		for(int i = 0; i < rowCount(); ++i) {
			msg += "[";
			for(int j = 0; j < columnCount(); ++j) {
				msg += get(i, j);
				if(j + 1 != columnCount()) {
					msg += ", ";
				}
			}
			msg += i + 1 == rowCount() ? "]" : "], ";
		}
		return msg + "]";
	}
	
	private int rows;
	private int columns;
	private float[][] data;
}
