package kaptainwutax.mathutils.lattice;

import kaptainwutax.mathutils.arithmetic.Rational;
import kaptainwutax.mathutils.component.Matrix;
import kaptainwutax.mathutils.component.Vector;

import java.security.InvalidParameterException;

public final class LLL {

    public static final Rational MIN_DELTA = new Rational(1, 4);
    public static final Rational MAX_DELTA = Rational.ONE;

    public static boolean supports(Matrix basis) {
        return true;
    }

    private static Matrix reduce(Matrix basis) {
        return reduceAndSet(basis.copy());
    }

    private static Matrix reduce(Matrix basis, Rational delta) {
        return reduceAndSet(basis.copy(), delta);
    }

    private static Matrix reduceAndSet(Matrix basis) {
        return reduceAndSet(basis, new Rational(99, 100));
    }

    public static Matrix reduceAndSet(Matrix basis, Rational delta) {
        if(delta.compareTo(MIN_DELTA) <= 0 && delta.compareTo(MAX_DELTA) > 0) {
            throw new InvalidParameterException("Delta must be in the range of (0.25, 1]");
        }

        Matrix newBasis = Matrix.zero(basis.getRowCount(), basis.getColumnCount());
        Matrix coefficients = Matrix.zero(basis.getRowCount(), basis.getColumnCount());
        updateGramSchmidt(basis, newBasis, coefficients);

        for(int k = 1; k < basis.getRowCount(); ) {
            for(int j = k - 1; j >= 0; j--) {
                Rational rounded = coefficients.get(k, j).round();
                if(rounded.signum() == 0)continue;
                basis.getRow(k).subtractAndSet(basis.getRow(j).scale(rounded));
                updateGramSchmidt(basis, newBasis, coefficients);
            }

            Vector prevRow = newBasis.getRow(k - 1);
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

    private static void updateGramSchmidt(Matrix basis, Matrix newBasis, Matrix coefficients) {
        for(int i = 0; i < basis.getRowCount(); i++) {
            Vector row = basis.getRowCopy(i);

            for(int j = 0; j < i; j++) {
                Vector target = newBasis.getRow(j);
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
