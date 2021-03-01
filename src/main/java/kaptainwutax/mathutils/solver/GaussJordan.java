package kaptainwutax.mathutils.solver;

import kaptainwutax.mathutils.arithmetic.Rational;
import kaptainwutax.mathutils.component.matrix.QMatrix;
import kaptainwutax.mathutils.component.vector.QVector;

public class GaussJordan {

	public static QMatrix solve(QMatrix.Augmented matrix, Phase phase) {
		return solveInternal(matrix.copy(), matrix.getSplit(), phase == Phase.REDUCED);
	}

	public static QMatrix solveAndSet(QMatrix.Augmented matrix, Phase phase) {
		return solveInternal(matrix, matrix.getSplit(), phase == Phase.REDUCED);
	}

	private static QMatrix solveInternal(QMatrix matrix, int split, boolean reduced) {
		int row = 0, column = 0;
		int[] pivots = new int[split];

		while(row < matrix.getRowCount() && column < split) {
			boolean foundPivot = false;

			for(int pivotRow = row; pivotRow < matrix.getRowCount(); pivotRow++) {
				if(!matrix.get(pivotRow, column).equals(Rational.ZERO)) {
					if(row != pivotRow) {
						matrix.swapRowsAndSet(row, pivotRow);
					}

					pivots[column] = row;
					foundPivot = true;
					break;
				}
			}

			if(!foundPivot) {
				pivots[column] = -1;
				column++;
				continue;
			}

			QVector main = matrix.getRow(row);
			main.scaleAndSet(Rational.ONE.divide(main.get(column)));

			for(int i = row + 1; i < matrix.getRowCount(); i++) {
				Rational value = matrix.get(i, column);
				if(value.equals(Rational.ZERO))continue;
				matrix.getRow(i).subtractAndSet(main.scale(value));
			}

			row++;
			column++;
		}

		if(reduced) {
			for(int columnPivot = column - 1; columnPivot >= 0; columnPivot--) {
				int pivot = pivots[columnPivot];
				if(pivot == -1)continue;

				QVector main = matrix.getRow(pivot);

				for(int i = 0; i < pivot; i++) {
					Rational value = matrix.get(i, columnPivot);
					if(value.equals(Rational.ZERO))continue;
					matrix.getRow(i).subtractAndSet(main.scale(value));
				}
			}
		}

		return matrix;
	}

	public enum Phase {
		ECHELON, REDUCED
	}

}
