package kaptainwutax.mathutils.component.vector;

import kaptainwutax.mathutils.arithmetic.Rational;
import kaptainwutax.mathutils.arithmetic.Real;
import kaptainwutax.mathutils.component.Norm;
import kaptainwutax.mathutils.component.matrix.QMatrix;

import java.math.BigInteger;
import java.util.Arrays;

public class QVector {

    public static final Norm<QVector, Rational> SUM = v -> {
        Rational sum = Rational.ZERO;

        for(int i = 0; i < v.getDimension(); i++) {
            sum = sum.add(v.get(i));
        }

        return sum;
    };

    public static final Norm<QVector, Rational> EUCLIDEAN_SQ = v -> {
        Rational sum = Rational.ZERO;

        for(int i = 0; i < v.getDimension(); i++) {
            sum = sum.add(v.get(i).multiply(v.get(i)));
        }

        return sum;
    };

    private final Rational[] elements;

    protected QVector(int dimension) {
        this.elements = new Rational[dimension];
    }

    public QVector(int dimension, Generator generator) {
        this(dimension);

        for(int i = 0; i < this.elements.length; i++) {
            this.elements[i] = generator.getValue(i);
        }
    }

    public QVector(Rational... elements) {
        this.elements = elements;
    }

    public QVector(BigInteger... elements) {
        this(Arrays.stream(elements).map(Rational::of).toArray(Rational[]::new));
    }

    public QVector(long... elements) {
        this(Arrays.stream(elements).mapToObj(Rational::of).toArray(Rational[]::new));
    }

    public static QVector zero(int dimension) {
        return new QVector(dimension, i -> Rational.ZERO);
    }

    public static QVector basis(int dimension, int index) {
        return basis(dimension, index, Rational.ONE);
    }

    public static QVector basis(int dimension, int index, Rational scale) {
        return new QVector(dimension, i -> i == index ? scale : Rational.ZERO);
    }

    public static QVector basis(int dimension, int index, BigInteger scale) {
        return basis(dimension, index, Rational.of(scale));
    }

    public static QVector basis(int dimension, int index, long scale) {
        return basis(dimension, index, Rational.of(scale));
    }

    public int getDimension() {
        return this.elements.length;
    }

    public Generator toGenerator() {
        return this::get;
    }

    public Mapper toMapper() {
        return this.toGenerator().asMapper();
    }

    public Rational get(int index) {
        return this.elements[index];
    }

    public QVector set(int index, Rational value) {
        this.elements[index] = value;
        return this;
    }

    public Rational[] getElements() {
        Rational[] elements = new Rational[this.getDimension()];

        for(int i = 0; i < this.getDimension(); i++) {
            elements[i] = this.get(i);
        }

        return elements;
    }

    public QVector with(int index, Rational value) {
        return this.copy().set(index, value);
    }

    public QVector map(Mapper mapper) {
        return new QVector(this.getDimension(), index -> mapper.getNewValue(index, this.get(index)));
    }

    public QVector mapAndSet(Mapper mapper) {
        for(int i = 0; i < this.getDimension(); i++) {
            this.set(i, mapper.getNewValue(i, this.get(i)));
        }

        return this;
    }

    protected void checkDimension(QVector other) {
        if(this.getDimension() != other.getDimension()) {
            throw new IllegalArgumentException("vectors don't have the same size");
        }
    }

    public Rational norm(Norm<QVector, Rational> norm) {
        return norm.get(this);
    }

    public Rational sum() {
        return this.norm(SUM);
    }

    public Rational magnitudeSq() {
        return this.norm(EUCLIDEAN_SQ);
    }

    public Rational raisedNorm(int p) {
        Rational sum = Rational.ZERO;

        for(int i = 0; i < this.getDimension(); i++) {
            Rational e = this.get(i);
            if(p == 1)sum = sum.add(e);
            else if(p == 2)sum = sum.add(e.multiply(e));
            else sum = sum.add(e.pow(p));
        }

        return sum;
    }

    public QVector normalize(Norm<QVector, Rational> norm) {
        Rational magnitude = norm.get(this);
        return magnitude.equals(Real.ZERO) ? this.copy() : this.map((index, oldValue) -> oldValue.divide(magnitude));
    }

    public QVector normalizeAndSet(Norm<QVector, Rational> norm) {
        Rational magnitude = norm.get(this);
        return magnitude.equals(Real.ZERO) ? this : this.mapAndSet((index, oldValue) -> oldValue.divide(magnitude));
    }

    public QVector swap(int i, int j) {
        return this.copy().set(i, this.get(j)).set(j, this.get(i));
    }

    public QVector swapAndSet(int i, int j) {
        Rational oldValue = this.get(i);
        return this.set(i, this.get(j)).set(j, oldValue);
    }

    public QVector add(QVector other) {
        this.checkDimension(other);
        return this.map((index, oldValue) -> oldValue.add(other.get(index)));
    }

    public QVector addAndSet(QVector other) {
        this.checkDimension(other);
        return this.mapAndSet((index, oldValue) -> oldValue.add(other.get(index)));
    }

    public QVector subtract(QVector other) {
        this.checkDimension(other);
        return this.map((index, oldValue) -> oldValue.subtract(other.get(index)));
    }

    public QVector subtractAndSet(QVector other) {
        this.checkDimension(other);
        return this.mapAndSet((index, oldValue) -> oldValue.subtract(other.get(index)));
    }

    public QVector scale(Rational scalar) {
        return this.map((index, oldValue) -> oldValue.multiply(scalar));
    }

    public QVector scaleAndSet(Rational scalar) {
        return this.mapAndSet((index, oldValue) -> oldValue.multiply(scalar));
    }

    public QVector scale(BigInteger scalar) {
        return this.map((index, oldValue) -> oldValue.multiply(scalar));
    }

    public QVector scaleAndSet(BigInteger scalar) {
        return this.mapAndSet((index, oldValue) -> oldValue.multiply(scalar));
    }

    public QVector scale(long scalar) {
        return this.map((index, oldValue) -> oldValue.multiply(scalar));
    }

    public QVector scaleAndSet(long scalar) {
        return this.mapAndSet((index, oldValue) -> oldValue.multiply(scalar));
    }

    public QVector multiply(QMatrix matrix) {
        if(matrix.getRowCount() != this.getDimension()) {
            throw new IllegalArgumentException("Vector length should equal the number of matrix columns");
        }

        return new QVector(this.getDimension(), i -> this.dot(matrix.getRow(i)));
    }

    public QVector multiplyAndSet(QMatrix matrix) {
        if(matrix.getRowCount() != this.getDimension()) {
            throw new IllegalArgumentException("Vector length should equal the number of matrix columns");
        }

        QVector original = this.copy();
        return this.mapAndSet((index, oldValue) -> original.dot(matrix.getRow(index)));
    }

    public QVector divide(Rational scalar) {
        return this.map((index, oldValue) -> oldValue.divide(scalar));
    }

    public QVector divideAndSet(Rational scalar) {
        return this.mapAndSet((index, oldValue) -> oldValue.divide(scalar));
    }

    public QVector divide(BigInteger scalar) {
        return this.map((index, oldValue) -> oldValue.divide(scalar));
    }

    public QVector divideAndSet(BigInteger scalar) {
        return this.mapAndSet((index, oldValue) -> oldValue.divide(scalar));
    }

    public QVector divideAndSet(long scalar) {
        return this.mapAndSet((index, oldValue) -> oldValue.divide(scalar));
    }

    public Rational dot(QVector other) {
        this.checkDimension(other);
        return new QVector(this.getDimension(), index -> this.get(index).multiply(other.get(index))).sum();
    }

    public QVector projectOnto(QVector other) {
        return other.scale(this.gramSchmidtCoefficient(other));
    }

    public QVector projectOnto(QMatrix other) {
        QMatrix transposed = other.transpose();
        return this.multiply(other.multiply(transposed.multiply(other).invert()).multiply(transposed));
    }

    public Rational gramSchmidtCoefficient(QVector other) {
        return this.dot(other).divide(other.magnitudeSq());
    }

    public QVector tensor(QVector other) {
        QVector res = new QVector(this.getDimension() * other.getDimension());

        for(int i = 0; i < this.getDimension(); i++) {
            for(int j = 0; j < other.getDimension(); j++) {
                int id = i * other.getDimension() + j;
                res.set(id, this.get(i).multiply(other.get(j)));
            }
        }

        return res;
    }

    public QMatrix toMatrixRow() {
        return new QMatrix(1, this.getDimension(), (row, column) -> this.get(column));
    }

    public QMatrix toMatrixColumn() {
        return new QMatrix(this.getDimension(), 1, (row, column) -> this.get(row));
    }

    public QVector copy() {
        return new QVector(this.getDimension(), this.toGenerator());
    }

    @Override
    public int hashCode() {
        return this.getDimension() * 31 + Arrays.hashCode(this.getElements());
    }

    @Override
    public boolean equals(Object other) {
        if(this == other)return true;
        if(!(other instanceof QVector))return false;
        QVector vector = (QVector)other;
        if(this.getDimension() != vector.getDimension())return false;

        for(int i = 0; i < this.getDimension(); i++) {
            if(!this.get(i).equals(vector.get(i)))return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return Arrays.toString(this.getElements());
    }

    public static class View extends QVector {
        private final int dimension;
        private final Generator getter;
        private final Setter setter;

        public View(int dimension, Generator getter, Setter setter) {
            super((Rational[])null);
            this.dimension = dimension;
            this.getter = getter;
            this.setter = setter;
        }

        @Override
        public int getDimension() {
            return this.dimension;
        }

        @Override
        public Rational get(int index) {
            return this.getter.getValue(index);
        }

        @Override
        public QVector set(int index, Rational value) {
            this.setter.set(index, value);
            return this;
        }

        @FunctionalInterface
        public interface Setter {
            void set(int index, Rational value);
        }
    }

    @FunctionalInterface
    public interface Generator {
        Rational getValue(int index);

        default Mapper asMapper() {
            return (index, oldValue) -> this.getValue(index);
        }
    }

    @FunctionalInterface
    public interface Mapper {
        Rational getNewValue(int index, Rational oldValue);

        default Generator asGenerator() {
            return index -> this.getNewValue(index, null);
        }
    }

}
