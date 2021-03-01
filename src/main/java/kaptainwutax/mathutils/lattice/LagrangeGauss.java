package kaptainwutax.mathutils.lattice;

import kaptainwutax.mathutils.arithmetic.Rational;
import kaptainwutax.mathutils.component.matrix.QMatrix;
import kaptainwutax.mathutils.component.vector.QVector;

public final class LagrangeGauss {

    public static boolean supports(QMatrix basis) {
        return basis.getRowCount() == 2 && basis.getColumnCount() == 2;
    }

    public static QMatrix reduce(QMatrix basis) {
        return reduceAndSet(basis.copy());
    }

    public static QMatrix reduceAndSet(QMatrix basis) {
        QVector minVec, maxVec, v1 = basis.getRow(0), v2 = basis.getRow(1);

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

            QVector temp = minVec; minVec = maxVec; maxVec = temp; //swap minVec and maxVec
        } while(minNorm.compareTo(maxNorm) > 0);

        return basis;
    }

}
