package kaptainwutax.mathutils.lattice;

import kaptainwutax.mathutils.Rational;
import kaptainwutax.mathutils.component.Matrix;
import kaptainwutax.mathutils.component.Vector;

public final class LagrangeGauss {

    public static boolean supports(Matrix basis) {
        return basis.getRowCount() == 2 && basis.getColumnCount() == 2;
    }

    public static Matrix reduce(Matrix basis) {
        return reduceAndSet(basis.copy());
    }

    public static Matrix reduceAndSet(Matrix basis) {
        Vector minVec, maxVec, v1 = basis.getRow(0), v2 = basis.getRow(1);

        if(v1.magnitudeSq().compareTo(v2.magnitudeSq()) <= 0) {
            minVec = v1; maxVec = v2;
        } else {
            minVec = v2; maxVec = v1;
        }

        Rational minNorm, maxNorm;

        do {
            minNorm = minVec.magnitudeSq();
            maxVec.subtractAndSet(minVec.scale(minVec.dot(maxVec).divide(minNorm).round()));
            maxNorm = maxVec.magnitudeSq();

            Vector temp = minVec; minVec = maxVec; maxVec = temp; //swap minVec and maxVec
        } while(minNorm.compareTo(maxNorm) > 0);

        return basis;
    }

}
