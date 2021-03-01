package kaptainwutax.mathutils.decomposition;

import kaptainwutax.mathutils.arithmetic.Rational;
import kaptainwutax.mathutils.arithmetic.Real;
import kaptainwutax.mathutils.component.matrix.QMatrix;
import kaptainwutax.mathutils.component.matrix.RMatrix;

public class LUDecomposition {

	public static LUDecomposition.Q of(QMatrix matrix) {
		return new LUDecomposition.Q(matrix);
	}

	public static LUDecomposition.R of(RMatrix matrix) {
		return new LUDecomposition.R(matrix);
	}

	public static class Q {
		private final QMatrix matrix;
		private final int size;

		private QMatrix P;
		private QMatrix L;
		private QMatrix U;
		private QMatrix LU;
		private boolean singular;
		private int swaps;
		private int[] pivot;
		private Rational det;
		private QMatrix inv;

		protected Q(QMatrix matrix) {
			if(!matrix.isSquare()) {
				throw new IllegalArgumentException("Matrix is not square");
			}

			this.matrix = matrix;
			this.size = this.matrix.getRowCount();
		}

		public Q refresh() {
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

		public QMatrix getMatrix() {
			return this.matrix;
		}

		public int getSize() {
			return this.size;
		}

		public QMatrix getP() {
			if(this.P != null)return this.P;
			int[] pivot = this.getPivot();
			if(pivot == null)return null;

			this.P = QMatrix.identity(this.size);

			for(int i = 0; i < this.size; i++) {
				this.P.swapRowsAndSet(i, pivot[i]);
			}

			return this.P;
		}

		public QMatrix getL() {
			if(this.L != null)return this.L;
			QMatrix lu = this.getLU();
			if(lu == null)return null;
			return this.L = lu.map((row, column, oldValue) -> row > column ? oldValue :
					row == column ? Rational.ONE : Rational.ZERO);
		}

		public QMatrix getU() {
			if(this.U != null)return this.U;
			QMatrix lu = this.getLU();
			if(lu == null)return null;
			return this.U = lu.map((row, col, oldValue) -> row <= col ? oldValue : Rational.ZERO);
		}

		public boolean isSingular() {
			this.getLU(); //Compute LU, singular, swaps and pivot.
			return this.singular;
		}

		public QMatrix getLU() {
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
			QMatrix lu = this.getLU();

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

		public QMatrix getInverse() {
			if(this.inv != null)return this.inv;
			QMatrix lu = this.getLU();
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

	public static class R {
		private final RMatrix matrix;
		private final int size;

		private RMatrix P;
		private RMatrix L;
		private RMatrix U;
		private RMatrix LU;
		private boolean singular;
		private int swaps;
		private int[] pivot;
		private Real det;
		private RMatrix inv;

		protected R(RMatrix matrix) {
			if(!matrix.isSquare()) {
				throw new IllegalArgumentException("Matrix is not square");
			}

			this.matrix = matrix;
			this.size = this.matrix.getRowCount();
		}

		public R refresh() {
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

		public RMatrix getMatrix() {
			return this.matrix;
		}

		public int getSize() {
			return this.size;
		}

		public RMatrix getP() {
			if(this.P != null)return this.P;
			int[] pivot = this.getPivot();
			if(pivot == null)return null;

			this.P = RMatrix.identity(this.size);

			for(int i = 0; i < this.size; i++) {
				this.P.swapRowsAndSet(i, pivot[i]);
			}

			return this.P;
		}

		public RMatrix getL() {
			if(this.L != null)return this.L;
			RMatrix lu = this.getLU();
			if(lu == null)return null;
			return this.L = lu.map((row, column, oldValue) -> row > column ? oldValue :
					row == column ? Real.ONE : Real.ZERO);
		}

		public RMatrix getU() {
			if(this.U != null)return this.U;
			RMatrix lu = this.getLU();
			if(lu == null)return null;
			return this.U = lu.map((row, col, oldValue) -> row <= col ? oldValue : Real.ZERO);
		}

		public boolean isSingular() {
			this.getLU(); //Compute LU, singular, swaps and pivot.
			return this.singular;
		}

		public RMatrix getLU() {
			if(this.LU != null || this.singular)return this.LU;
			this.LU = this.matrix.copy();
			this.pivot = new int[this.size];
			this.swaps = 0;

			for(int i = 0; i < this.size; i++) {
				int pivot = -1;
				Real largest = Real.ZERO;

				for(int row = i; row < this.size; row++) {
					Real value = this.LU.get(row, i).abs();

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
					Real divisor = this.LU.get(i, i);
					this.LU.set(row, i, this.LU.get(row, i).divide(divisor));
				}

				for(int row = i + 1; row < this.size; row++) {
					for(int column = i + 1; column < this.size; column++) {
						Real subtrahend = this.LU.get(row, i).multiply(this.LU.get(i, column));
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

		public Real getDeterminant() {
			if(this.det != null)return this.det;
			RMatrix lu = this.getLU();

			if(!this.isSingular()) {
				this.det = Real.ONE;

				for(int i = 0; i < this.size; i++) {
					this.det = this.det.multiply(lu.get(i, i));
				}

				if((this.getSwaps() & 1) != 0) {
					this.det = this.det.negate();
				}
			} else {
				this.det = Real.ZERO;
			}

			return this.det;
		}

		public RMatrix getInverse() {
			if(this.inv != null)return this.inv;
			RMatrix lu = this.getLU();
			if(lu == null)return null;

			this.inv = this.getP().copy();

			for(int dcol = 0; dcol < this.size; dcol++) {
				for(int row = 0; row < this.size; row++) {
					Real value = this.inv.get(row, dcol);

					for(int col = 0; col < row; col++) {
						value = value.subtract(lu.get(row, col).multiply(this.inv.get(col, dcol)));
					}

					this.inv.set(row, dcol, value);
				}
			}

			for(int dcol = 0; dcol < this.size; dcol++) {
				for(int row = this.size - 1; row >= 0; row--) {
					Real value = this.inv.get(row, dcol);

					for(int col = this.size - 1; col > row; col--) {
						value = value.subtract(lu.get(row, col).multiply(this.inv.get(col, dcol)));
					}

					this.inv.set(row, dcol, value.divide(lu.get(row, row)));
				}
			}

			return this.inv;
		}
	}

}
