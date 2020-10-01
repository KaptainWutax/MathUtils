package kaptainwutax.mathutils.decomposition;

import kaptainwutax.mathutils.arithmetic.Rational;
import kaptainwutax.mathutils.component.Matrix;

public class LUDecomposition {

	private final Matrix matrix;
	private final int size;

	private Matrix P;
	private Matrix L;
	private Matrix U;
	private Matrix LU;
	private boolean singular;
	private int swaps;
	private int[] pivot;
	private Rational det;
	private Matrix inv;

	public LUDecomposition(Matrix matrix) {
		if(!matrix.isSquare()) {
			throw new IllegalArgumentException("Matrix is not square");
		}

		this.matrix = matrix;
		this.size = this.matrix.getRowCount();
	}

	public LUDecomposition refresh() {
		this.P = null;
		this.L = null;
		this.U = null;
		this.LU = null;
		this.singular = false;
		this.swaps = -1;
		this.pivot = null;
		this.det = null;
		this.inv = null;
		return this;
	}

	public Matrix getMatrix() {
		return this.matrix;
	}

	public int getSize() {
		return this.size;
	}

	public Matrix getP() {
		if(this.P != null)return this.P;
		int[] pivot = this.getPivot();
		if(pivot == null)return null;

		this.P = Matrix.identity(this.size);

		for(int i = 0; i < this.size; i++) {
			this.P.swapRowsAndSet(i, pivot[i]);
		}

		return this.P;
	}

	public Matrix getL() {
		if(this.L != null)return this.L;
		Matrix lu = this.getLU();
		if(lu == null)return null;
		return this.L = lu.map((row, column, oldValue) -> row > column ? oldValue :
				row == column ? Rational.ONE : Rational.ZERO);
	}

	public Matrix getU() {
		if(this.U != null)return this.U;
		Matrix lu = this.getLU();
		if(lu == null)return null;
		return this.U = lu.map((row, col, oldValue) -> row <= col ? oldValue : Rational.ZERO);
	}

	public boolean isSingular() {
		this.getLU(); //Compute LU, singular, swaps and pivot.
		return this.singular;
	}

	public Matrix getLU() {
		if(this.LU != null || this.singular)return this.LU;
		this.LU = this.matrix.copy();
		this.pivot = new int[this.size];
		this.swaps = 0;

		for(int i = 0; i < this.size; i++) {
			int pivot = -1;
			Rational largest = Rational.ZERO;

			for(int row = i; row < this.size; row++) {
				Rational value = this.LU.get(row, i).abs();

				if(value.signum() != 0 && value.compareTo(largest) > 0) {
					largest = value;
					pivot = row;
				}
			}

			if(pivot == -1) {
				this.singular = true;
				this.LU = null;
				this.pivot = null;
				this.swaps = -1;
				return null;
			}

			this.pivot[i] = pivot;

			if(pivot != i) {
				this.LU.swapRowsAndSet(i, pivot);
				this.swaps++;
			}

			for(int row = i + 1; row < this.size; row++) {
				Rational divisor = this.LU.get(i, i);
				this.LU.set(row, i, this.LU.get(row, i).divide(divisor));
			}

			for(int row = i + 1; row < this.size; row++) {
				for(int column = i + 1; column < this.size; column++) {
					Rational subtrahend = this.LU.get(row, i).multiply(this.LU.get(i, column));
					this.LU.set(row, column, this.LU.get(row, column).subtract(subtrahend));
				}
			}
		}

		return this.LU;
	}

	public int getSwaps() {
		if(this.LU != null)return this.swaps;
		this.getLU(); //Compute LU, singular, swaps and pivot.
		return this.swaps;
	}

	public int[] getPivot() {
		if(this.pivot != null)return this.pivot;
		this.getLU(); //Compute LU, singular, swaps and pivot.
		return this.pivot;
	}

	public Rational getDeterminant() {
		if(this.det != null)return this.det;
		Matrix lu = this.getLU();

		if(!this.isSingular()) {
			this.det = Rational.ONE;

			for(int i = 0; i < this.size; i++) {
				this.det = this.det.multiply(lu.get(i, i));
			}

			if((this.getSwaps() & 1) != 0) {
				this.det = this.det.negate();
			}
		} else {
			this.det = Rational.ZERO;
		}

		return this.det;
	}

	public Matrix getInverse() {
		if(this.inv != null)return this.inv;
		Matrix lu = this.getLU();
		if(lu == null)return null;

		this.inv = this.getP().copy();

		for(int dcol = 0; dcol < this.size; dcol++) {
			for(int row = 0; row < this.size; row++) {
				Rational value = this.inv.get(row, dcol);

				for(int col = 0; col < row; col++) {
					value = value.subtract(lu.get(row, col).multiply(this.inv.get(col, dcol)));
				}

				this.inv.set(row, dcol, value);
			}
		}

		for(int dcol = 0; dcol < this.size; dcol++) {
			for(int row = this.size - 1; row >= 0; row--) {
				Rational value = this.inv.get(row, dcol);

				for(int col = this.size - 1; col > row; col--) {
					value = value.subtract(lu.get(row, col).multiply(this.inv.get(col, dcol)));
				}

				this.inv.set(row, dcol, value.divide(lu.get(row, row)));
			}
		}

		return this.inv;
	}

}
