package kaptainwutax.mathutils.component.vector;

import kaptainwutax.mathutils.arithmetic.Rational;
import kaptainwutax.mathutils.arithmetic.Real;
import kaptainwutax.mathutils.component.Norm;
import kaptainwutax.mathutils.component.matrix.QMatrix;
import kaptainwutax.mathutils.component.matrix.RMatrix;

import java.math.BigInteger;
import java.util.Arrays;

public class RVector {

    public static final Norm<RVector, Real> SUM = v -> {
        Real sum = Real.ZERO;

        for(int i = 0; i < v.getDimension(); i++) {
            sum = sum.add(v.get(i));
        }

        return sum;
    };

    public static final Norm<RVector, Real> EUCLIDEAN_SQ = v -> {
        Real sum = Real.ZERO;

        for(int i = 0; i < v.getDimension(); i++) {
            sum = sum.add(v.get(i).multiply(v.get(i)));
        }

        return sum;
    };

    public static final Norm<RVector, Real> EUCLIDEAN = v -> {
        return v.getDimension() == 0 ? Real.ZERO : EUCLIDEAN_SQ.get(v).sqrt();
    };

    private final Real[] elements;

    protected RVector(int dimension) {
        this.elements = new Real[dimension];
    }

    public RVector(int dimension, Generator generator) {
        this(dimension);

        for(int i = 0; i < this.elements.length; i++) {
            this.elements[i] = generator.getValue(i);
        }
    }

    public RVector(Real... elements) {
        this.elements = elements;
    }

    public RVector(BigInteger... elements) {
        this(Arrays.stream(elements).map(Real::of).toArray(Real[]::new));
    }

    public RVector(long... elements) {
        this(Arrays.stream(elements).mapToObj(Real::of).toArray(Real[]::new));
    }

    public static RVector zero(int dimension) {
        return new RVector(dimension, i -> Real.ZERO);
    }

    public static RVector basis(int dimension, int index) {
        return basis(dimension, index, Real.ONE);
    }

    public static RVector basis(int dimension, int index, Real scale) {
        return new RVector(dimension, i -> i == index ? scale : Real.ZERO);
    }

    public static RVector basis(int dimension, int index, BigInteger scale) {
        return basis(dimension, index, Real.of(scale));
    }

    public static RVector basis(int dimension, int index, long scale) {
        return basis(dimension, index, Real.of(scale));
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

    public Real get(int index) {
        return this.elements[index];
    }

    public RVector set(int index, Real value) {
        this.elements[index] = value;
        return this;
    }

    public Real[] getElements() {
        Real[] elements = new Real[this.getDimension()];

        for(int i = 0; i < this.getDimension(); i++) {
            elements[i] = this.get(i);
        }

        return elements;
    }

    public RVector with(int index, Real value) {
        return this.copy().set(index, value);
    }

    public RVector map(Mapper mapper) {
        return new RVector(this.getDimension(), index -> mapper.getNewValue(index, this.get(index)));
    }

    public RVector mapAndSet(Mapper mapper) {
        for(int i = 0; i < this.getDimension(); i++) {
            this.set(i, mapper.getNewValue(i, this.get(i)));
        }

        return this;
    }

    protected void checkDimension(RVector other) {
        if(this.getDimension() != other.getDimension()) {
            throw new IllegalArgumentException("vectors don't have the same size");
        }
    }

    public Real norm(Norm<RVector, Real> norm) {
        return norm.get(this);
    }

    public Real sum() {
        return this.norm(SUM);
    }

    public Real magnitude() {
        return this.norm(EUCLIDEAN);
    }

    public Real magnitudeSq() {
        return this.norm(EUCLIDEAN_SQ);
    }

    public RVector normalize(Norm<RVector, Real> norm) {
        Real magnitude = norm.get(this);
        return magnitude.equals(Real.ZERO) ? this.copy() : this.map((index, oldValue) -> oldValue.divide(magnitude));
    }

    public RVector normalizeAndSet(Norm<RVector, Real> norm) {
        Real magnitude = norm.get(this);
        return magnitude.equals(Real.ZERO) ? this : this.mapAndSet((index, oldValue) -> oldValue.divide(magnitude));
    }

    public RVector swap(int i, int j) {
        return this.copy().set(i, this.get(j)).set(j, this.get(i));
    }

    public RVector swapAndSet(int i, int j) {
        Real oldValue = this.get(i);
        return this.set(i, this.get(j)).set(j, oldValue);
    }

    public RVector add(RVector other) {
        this.checkDimension(other);
        return this.map((index, oldValue) -> oldValue.add(other.get(index)));
    }

    public RVector addAndSet(RVector other) {
        this.checkDimension(other);
        return this.mapAndSet((index, oldValue) -> oldValue.add(other.get(index)));
    }

    public RVector subtract(RVector other) {
        this.checkDimension(other);
        return this.map((index, oldValue) -> oldValue.subtract(other.get(index)));
    }

    public RVector subtractAndSet(RVector other) {
        this.checkDimension(other);
        return this.mapAndSet((index, oldValue) -> oldValue.subtract(other.get(index)));
    }

    public RVector scale(Real scalar) {
        return this.map((index, oldValue) -> oldValue.multiply(scalar));
    }

    public RVector scaleAndSet(Real scalar) {
        return this.mapAndSet((index, oldValue) -> oldValue.multiply(scalar));
    }

    public RVector scale(BigInteger scalar) {
        return this.map((index, oldValue) -> oldValue.multiply(scalar));
    }

    public RVector scaleAndSet(BigInteger scalar) {
        return this.mapAndSet((index, oldValue) -> oldValue.multiply(scalar));
    }

    public RVector scale(long scalar) {
        return this.map((index, oldValue) -> oldValue.multiply(scalar));
    }

    public RVector scaleAndSet(long scalar) {
        return this.mapAndSet((index, oldValue) -> oldValue.multiply(scalar));
    }

    public RVector multiply(RMatrix matrix) {
        if(matrix.getRowCount() != this.getDimension()) {
            throw new IllegalArgumentException("Vector length should equal the number of matrix columns");
        }

        return new RVector(this.getDimension(), i -> this.dot(matrix.getRow(i)));
    }

    public RVector multiplyAndSet(RMatrix matrix) {
        if(matrix.getRowCount() != this.getDimension()) {
            throw new IllegalArgumentException("Vector length should equal the number of matrix columns");
        }

        RVector original = this.copy();
        return this.mapAndSet((index, oldValue) -> original.dot(matrix.getRow(index)));
    }

    public RVector divide(Real scalar) {
        return this.map((index, oldValue) -> oldValue.divide(scalar));
    }

    public RVector divideAndSet(Real scalar) {
        return this.mapAndSet((index, oldValue) -> oldValue.divide(scalar));
    }

    public RVector divide(BigInteger scalar) {
        return this.map((index, oldValue) -> oldValue.divide(scalar));
    }

    public RVector divideAndSet(BigInteger scalar) {
        return this.mapAndSet((index, oldValue) -> oldValue.divide(scalar));
    }

    public RVector divideAndSet(long scalar) {
        return this.mapAndSet((index, oldValue) -> oldValue.divide(scalar));
    }

    public Real dot(RVector other) {
        this.checkDimension(other);
        return new RVector(this.getDimension(), index -> this.get(index).multiply(other.get(index))).sum();
    }

    public RVector projectOnto(RVector other) {
        return other.scale(this.gramSchmidtCoefficient(other));
    }

    public RVector projectOnto(RMatrix other) {
        RMatrix transposed = other.transpose();
        return this.multiply(other.multiply(transposed.multiply(other).invert()).multiply(transposed));
    }

    public Real gramSchmidtCoefficient(RVector other) {
        return this.dot(other).divide(other.magnitudeSq());
    }

    public RVector tensor(RVector other) {
        RVector res = new RVector(this.getDimension() * other.getDimension());

        for(int i = 0; i < this.getDimension(); i++) {
            for(int j = 0; j < other.getDimension(); j++) {
                int id = i * other.getDimension() + j;
                res.set(id, this.get(i).multiply(other.get(j)));
            }
        }

        return res;
    }

    public RMatrix toMatrixRow() {
        return new RMatrix(1, this.getDimension(), (row, column) -> this.get(column));
    }

    public RMatrix toMatrixColumn() {
        return new RMatrix(this.getDimension(), 1, (row, column) -> this.get(row));
    }

    public RVector copy() {
        return new RVector(this.getDimension(), this.toGenerator());
    }

    @Override
    public int hashCode() {
        return this.getDimension() * 31 + Arrays.hashCode(this.getElements());
    }

    @Override
    public boolean equals(Object other) {
        if(this == other)return true;
        if(!(other instanceof RVector))return false;
        RVector vector = (RVector)other;
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

    public static class View extends RVector {
        private final int dimension;
        private final Generator getter;
        private final Setter setter;

        public View(int dimension, Generator getter, Setter setter) {
            super((Real[])null);
            this.dimension = dimension;
            this.getter = getter;
            this.setter = setter;
        }

        @Override
        public int getDimension() {
            return this.dimension;
        }

        @Override
        public Real get(int index) {
            return this.getter.getValue(index);
        }

        @Override
        public RVector set(int index, Real value) {
            this.setter.set(index, value);
            return this;
        }

        @FunctionalInterface
        public interface Setter {
            void set(int index, Real value);
        }
    }

    @FunctionalInterface
    public interface Generator {
        Real getValue(int index);

        default Mapper asMapper() {
            return (index, oldValue) -> this.getValue(index);
        }
    }

    @FunctionalInterface
    public interface Mapper {
        Real getNewValue(int index, Real oldValue);

        default Generator asGenerator() {
            return index -> this.getNewValue(index, null);
        }
    }

}
