package kaptainwutax.mathutils.component.vector;

import kaptainwutax.mathutils.arithmetic.Complex;
import kaptainwutax.mathutils.component.Norm;
import kaptainwutax.mathutils.component.matrix.CMatrix;

import java.math.BigInteger;
import java.util.Arrays;

public class CVector {

    public static final Norm<CVector, Complex> SUM = v -> {
        Complex sum = Complex.ZERO;

        for(int i = 0; i < v.getDimension(); i++) {
            sum = sum.add(v.get(i));
        }

        return sum;
    };

    private final Complex[] elements;

    protected CVector(int dimension) {
        this.elements = new Complex[dimension];
    }

    public CVector(int dimension, Generator generator) {
        this(dimension);

        for(int i = 0; i < this.elements.length; i++) {
            this.elements[i] = generator.getValue(i);
        }
    }

    public CVector(Complex... elements) {
        this.elements = elements;
    }

    public static CVector zero(int dimension) {
        return new CVector(dimension, i -> Complex.ZERO);
    }

    public static CVector basis(int dimension, int index) {
        return basis(dimension, index, Complex.ONE);
    }

    public static CVector basis(int dimension, int index, Complex scale) {
        return new CVector(dimension, i -> i == index ? scale : Complex.ZERO);
    }

    public static CVector basis(int dimension, int index, BigInteger scale) {
        return basis(dimension, index, Complex.of(scale));
    }

    public static CVector basis(int dimension, int index, long scale) {
        return basis(dimension, index, Complex.of(scale));
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

    public Complex get(int index) {
        return this.elements[index];
    }

    public CVector set(int index, Complex value) {
        this.elements[index] = value;
        return this;
    }

    public Complex[] getElements() {
        Complex[] elements = new Complex[this.getDimension()];

        for(int i = 0; i < this.getDimension(); i++) {
            elements[i] = this.get(i);
        }

        return elements;
    }

    public CVector with(int index, Complex value) {
        return this.copy().set(index, value);
    }

    public CVector map(Mapper mapper) {
        return new CVector(this.getDimension(), index -> mapper.getNewValue(index, this.get(index)));
    }

    public CVector mapAndSet(Mapper mapper) {
        for(int i = 0; i < this.getDimension(); i++) {
            this.set(i, mapper.getNewValue(i, this.get(i)));
        }

        return this;
    }

    protected void checkDimension(CVector other) {
        if(this.getDimension() != other.getDimension()) {
            throw new IllegalArgumentException("vectors don't have the same size");
        }
    }

    public Complex norm(Norm<CVector, Complex> norm) {
        return norm.get(this);
    }

    public Complex sum() {
        return this.norm(SUM);
    }

    public CVector swap(int i, int j) {
        return this.copy().set(i, this.get(j)).set(j, this.get(i));
    }

    public CVector swapAndSet(int i, int j) {
        Complex oldValue = this.get(i);
        return this.set(i, this.get(j)).set(j, oldValue);
    }

    public CVector add(CVector other) {
        this.checkDimension(other);
        return this.map((index, oldValue) -> oldValue.add(other.get(index)));
    }

    public CVector addAndSet(CVector other) {
        this.checkDimension(other);
        return this.mapAndSet((index, oldValue) -> oldValue.add(other.get(index)));
    }

    public CVector subtract(CVector other) {
        this.checkDimension(other);
        return this.map((index, oldValue) -> oldValue.subtract(other.get(index)));
    }

    public CVector subtractAndSet(CVector other) {
        this.checkDimension(other);
        return this.mapAndSet((index, oldValue) -> oldValue.subtract(other.get(index)));
    }

    public CVector scale(Complex scalar) {
        return this.map((index, oldValue) -> oldValue.multiply(scalar));
    }

    public CVector scaleAndSet(Complex scalar) {
        return this.mapAndSet((index, oldValue) -> oldValue.multiply(scalar));
    }

    public CVector scale(BigInteger scalar) {
        return this.map((index, oldValue) -> oldValue.multiply(scalar));
    }

    public CVector scaleAndSet(BigInteger scalar) {
        return this.mapAndSet((index, oldValue) -> oldValue.multiply(scalar));
    }

    public CVector scale(long scalar) {
        return this.map((index, oldValue) -> oldValue.multiply(scalar));
    }

    public CVector scaleAndSet(long scalar) {
        return this.mapAndSet((index, oldValue) -> oldValue.multiply(scalar));
    }

    public CVector multiply(CMatrix matrix) {
        if(matrix.getRowCount() != this.getDimension()) {
            throw new IllegalArgumentException("Vector length should equal the number of matrix columns");
        }

        return new CVector(this.getDimension(), i -> this.dot(matrix.getRow(i)));
    }

    public CVector multiplyAndSet(CMatrix matrix) {
        if(matrix.getRowCount() != this.getDimension()) {
            throw new IllegalArgumentException("Vector length should equal the number of matrix columns");
        }

        CVector original = this.copy();
        return this.mapAndSet((index, oldValue) -> original.dot(matrix.getRow(index)));
    }

    public CVector divide(Complex scalar) {
        return this.map((index, oldValue) -> oldValue.divide(scalar));
    }

    public CVector divideAndSet(Complex scalar) {
        return this.mapAndSet((index, oldValue) -> oldValue.divide(scalar));
    }

    public CVector divide(BigInteger scalar) {
        return this.map((index, oldValue) -> oldValue.divide(scalar));
    }

    public CVector divideAndSet(BigInteger scalar) {
        return this.mapAndSet((index, oldValue) -> oldValue.divide(scalar));
    }

    public CVector divideAndSet(long scalar) {
        return this.mapAndSet((index, oldValue) -> oldValue.divide(scalar));
    }

    public Complex dot(CVector other) {
        this.checkDimension(other);
        return new CVector(this.getDimension(), index -> this.get(index).multiply(other.get(index))).sum();
    }

    public CVector tensor(CVector other) {
        CVector res = new CVector(this.getDimension() * other.getDimension());

        for(int i = 0; i < this.getDimension(); i++) {
            for(int j = 0; j < other.getDimension(); j++) {
                int id = i * other.getDimension() + j;
                res.set(id, this.get(i).multiply(other.get(j)));
            }
        }

        return res;
    }

    public CMatrix toMatrixRow() {
        return new CMatrix(1, this.getDimension(), (row, column) -> this.get(column));
    }

    public CMatrix toMatrixColumn() {
        return new CMatrix(this.getDimension(), 1, (row, column) -> this.get(row));
    }

    public CVector copy() {
        return new CVector(this.getDimension(), this.toGenerator());
    }

    @Override
    public int hashCode() {
        return this.getDimension() * 31 + Arrays.hashCode(this.getElements());
    }

    @Override
    public boolean equals(Object other) {
        if(this == other)return true;
        if(!(other instanceof CVector))return false;
        CVector vector = (CVector)other;
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

    public static class View extends CVector {
        private final int dimension;
        private final Generator getter;
        private final Setter setter;

        public View(int dimension, Generator getter, Setter setter) {
            super((Complex[])null);
            this.dimension = dimension;
            this.getter = getter;
            this.setter = setter;
        }

        @Override
        public int getDimension() {
            return this.dimension;
        }

        @Override
        public Complex get(int index) {
            return this.getter.getValue(index);
        }

        @Override
        public CVector set(int index, Complex value) {
            this.setter.set(index, value);
            return this;
        }

        @FunctionalInterface
        public interface Setter {
            void set(int index, Complex value);
        }
    }

    @FunctionalInterface
    public interface Generator {
        Complex getValue(int index);

        default Mapper asMapper() {
            return (index, oldValue) -> this.getValue(index);
        }
    }

    @FunctionalInterface
    public interface Mapper {
        Complex getNewValue(int index, Complex oldValue);

        default Generator asGenerator() {
            return index -> this.getNewValue(index, null);
        }
    }

}
