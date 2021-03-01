package kaptainwutax.mathutils.lattice;

import kaptainwutax.mathutils.arithmetic.Rational;
import kaptainwutax.mathutils.component.matrix.QMatrix;
import kaptainwutax.mathutils.component.vector.QVector;

import java.security.InvalidParameterException;

public final class LLL {

    public static final Rational MIN_DELTA = Rational.of(1, 4);
    public static final Rational MAX_DELTA = Rational.ONE;

    public static final Rational DEFAULT_DELTA = Rational.of(99, 100);

    public static boolean supports(QMatrix basis) {
        return true;
    }

    public static QMatrix reduce(QMatrix basis) {
        return reduceAndSet(basis.copy());
    }

    public static QMatrix reduce(QMatrix basis, Rational delta) {
        return reduceAndSet(basis.copy(), delta);
    }

    public static QMatrix reduceAndSet(QMatrix basis) {
        return reduceAndSet(basis, DEFAULT_DELTA);
    }

    public static QMatrix reduceAndSet(QMatrix basis, Rational delta) {
        if(delta.compareTo(MIN_DELTA) <= 0 && delta.compareTo(MAX_DELTA) > 0) {
            throw new InvalidParameterException("Delta must be in the range of (0.25, 1]");
        }

        QMatrix newBasis = QMatrix.zero(basis.getRowCount(), basis.getColumnCount());
        QMatrix coefficients = QMatrix.zero(basis.getRowCount(), basis.getColumnCount());
        updateGramSchmidt(basis, newBasis, coefficients);

        for(int k = 1; k < basis.getRowCount(); ) {
            for(int j = k - 1; j >= 0; j--) {
                Rational rounded = coefficients.get(k, j).round();
                if(rounded.signum() == 0)continue;
                basis.getRow(k).subtractAndSet(basis.getRow(j).scale(rounded));
                updateGramSchmidt(basis, newBasis, coefficients);
            }

            QVector prevRow = newBasis.getRow(k - 1);
            Rational a = newBasis.getRow(k).magnitudeSq()
                    .add(prevRow.scale(coefficients.get(k, k - 1)).magnitudeSq());

            if(a.compareTo(prevRow.magnitudeSq().multiply(delta)) < 0) {
                basis.swapRowsAndSet(k - 1, k);
                updateGramSchmidt(basis, newBasis, coefficients);
                k = (k >= 2) ? k - 1 : 1;
                continue;
            }

            k++;
        }

        return basis;
    }

    private static void updateGramSchmidt(QMatrix basis, QMatrix newBasis, QMatrix coefficients) {
        for(int i = 0; i < basis.getRowCount(); i++) {
            QVector row = basis.getRowCopy(i);

            for(int j = 0; j < i; j++) {
                QVector target = newBasis.getRow(j);
                coefficients.set(i, j, basis.getRow(i).gramSchmidtCoefficient(target));
                row.subtractAndSet(target.scale(coefficients.get(i, j)));
            }

            newBasis.setRow(i, row);
        }

        for(int row = 0; row < basis.getRowCount(); row++) {
            for(int column = row; column < basis.getColumnCount(); column++) {
                coefficients.set(row, column, basis.getRow(row).gramSchmidtCoefficient(newBasis.getRow(column)));
            }
        }
    }

}
