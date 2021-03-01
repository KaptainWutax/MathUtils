package kaptainwutax.mathutils.solver;

import kaptainwutax.mathutils.arithmetic.Rational;
import kaptainwutax.mathutils.component.matrix.QMatrix;
import kaptainwutax.mathutils.component.vector.QVector;

public class GramSchmidt {

	public static void main(String[] args) {
		QMatrix m = new QMatrix(
				new QVector(4, 3),
				new QVector(-1, 6)
		).transpose();

		System.out.println(apply(m));
		System.out.println(m.getColumn(0).dot(m.getColumn(1)));
	}

	public static QMatrix apply(QMatrix matrix) {
		QMatrix result = new QMatrix(matrix.getRowCount(), matrix.getColumnCount(), (row, column) -> Rational.ZERO);

		for(int i = 0; i < matrix.getColumnCount(); i++) {
			QVector w = matrix.getColumn(i).copy();

			for(int j = 0; j < i; j++) {
				w.subtractAndSet(matrix.getColumn(i).projectOnto(result.getColumn(j)));
			}

			result.setColumn(i, w);
		}

		return result;
	}

	public enum Phase {
		ORTHOGONAL, ORTHONORMAL
	}

}
