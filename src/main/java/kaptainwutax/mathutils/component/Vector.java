package kaptainwutax.mathutils.component;

import kaptainwutax.mathutils.arithmetic.Rational;

import java.math.BigInteger;
import java.util.Arrays;

public class Vector {

    private final Rational[] elements;

    protected Vector(int dimension) {
        this.elements = new Rational[dimension];
    }

    public Vector(int dimension, Generator generator) {
        this(dimension);

        for(int i = 0; i < this.elements.length; i++) {
            this.elements[i] = generator.getValue(i);
        }
    }

    public Vector(Rational... elements) {
        this.elements = elements;
    }

    public static Vector zero(int dimension) {
        return new Vector(dimension, i -> Rational.ZERO);
    }

    public static Vector basis(int dimension, int index) {
        return basis(dimension, index, Rational.ONE);
    }

    public static Vector basis(int dimension, int index, Rational scale) {
        return new Vector(dimension, i -> i == index ? scale : Rational.ZERO);
    }

    public static Vector basis(int dimension, int index, BigInteger scale) {
        return basis(dimension, index, new Rational(scale));
    }

    public static Vector basis(int dimension, int index, long scale) {
        return basis(dimension, index, new Rational(scale));
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

    public Vector set(int index, Rational value) {
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

    public Vector with(int index, Rational value) {
        return this.copy().set(index, value);
    }

    public Vector map(Mapper mapper) {
        return new Vector(this.getDimension(), index -> mapper.getNewValue(index, this.get(index)));
    }

    public Vector mapAndSet(Mapper mapper) {
        for(int i = 0; i < this.getDimension(); i++) {
            this.set(i, mapper.getNewValue(i, this.get(i)));
        }

        return this;
    }

    protected void checkDimension(Vector other) {
        if(this.getDimension() != other.getDimension()) {
            throw new IllegalArgumentException("vectors don't have the same size");
        }
    }

    public Rational sum() {
        return this.raisedNorm(1);
    }

    public Rational magnitudeSq() {
        return this.raisedNorm(2);
    }

    public Rational raisedNorm(int p) {
        Rational sum = Rational.ZERO;

        for(int i = 0; i < this.getDimension(); i++) {
            Rational e = this.get(i);
            sum = sum.add(p == 1 ? e : e.pow(p));
        }

        return sum;
    }

    public Vector swap(int i, int j) {
        return this.copy().set(i, this.get(j)).set(j, this.get(i));
    }

    public Vector swapAndSet(int i, int j) {
        Rational oldValue = this.get(i);
        return this.set(i, this.get(j)).set(j, oldValue);
    }

    public Vector add(Vector other) {
        this.checkDimension(other);
        return this.map((index, oldValue) -> oldValue.add(other.get(index)));
    }

    public Vector addAndSet(Vector other) {
        this.checkDimension(other);
        return this.mapAndSet((index, oldValue) -> oldValue.add(other.get(index)));
    }

    public Vector subtract(Vector other) {
        this.checkDimension(other);
        return this.map((index, oldValue) -> oldValue.subtract(other.get(index)));
    }

    public Vector subtractAndSet(Vector other) {
        this.checkDimension(other);
        return this.mapAndSet((index, oldValue) -> oldValue.subtract(other.get(index)));
    }

    public Vector scale(Rational scalar) {
        return this.map((index, oldValue) -> oldValue.multiply(scalar));
    }

    public Vector scaleAndSet(Rational scalar) {
        return this.mapAndSet((index, oldValue) -> oldValue.multiply(scalar));
    }

    public Vector scale(BigInteger scalar) {
        return this.map((index, oldValue) -> oldValue.multiply(scalar));
    }

    public Vector scaleAndSet(BigInteger scalar) {
        return this.mapAndSet((index, oldValue) -> oldValue.multiply(scalar));
    }

    public Vector scale(long scalar) {
        return this.map((index, oldValue) -> oldValue.multiply(scalar));
    }

    public Vector scaleAndSet(long scalar) {
        return this.mapAndSet((index, oldValue) -> oldValue.multiply(scalar));
    }

    public Vector multiply(Matrix matrix) {
        if(matrix.getColumnCount() != this.getDimension()) {
            throw new IllegalArgumentException("Vector length should equal the number of matrix columns");
        }

        return new Vector(this.getDimension(), i -> this.dot(matrix.getColumnCopy(i)));
    }

    public Vector multiplyAndSet(Matrix matrix) {
        if(matrix.getColumnCount() != this.getDimension()) {
            throw new IllegalArgumentException("Vector length should equal the number of matrix columns");
        }

        Vector original = this.copy();
        return this.mapAndSet((index, oldValue) -> original.dot(matrix.getColumnCopy(index)));
    }

    public Vector divide(Rational scalar) {
        return this.map((index, oldValue) -> oldValue.divide(scalar));
    }

    public Vector divideAndSet(Rational scalar) {
        return this.mapAndSet((index, oldValue) -> oldValue.divide(scalar));
    }

    public Vector divide(BigInteger scalar) {
        return this.map((index, oldValue) -> oldValue.divide(scalar));
    }

    public Vector divideAndSet(BigInteger scalar) {
        return this.mapAndSet((index, oldValue) -> oldValue.divide(scalar));
    }

    public Vector divideAndSet(long scalar) {
        return this.mapAndSet((index, oldValue) -> oldValue.divide(scalar));
    }

    public Rational dot(Vector other) {
        this.checkDimension(other);
        return new Vector(this.getDimension(), index -> this.get(index).multiply(other.get(index))).sum();
    }

    public Rational gramSchmidtCoefficient(Vector other) {
        return this.dot(other).divide(other.magnitudeSq());
    }

    public Vector projectOnto(Vector other) {
        return other.scale(this.gramSchmidtCoefficient(other));
    }

    public Vector copy() {
        return new Vector(this.getDimension(), this.toGenerator());
    }

    @Override
    public int hashCode() {
        return this.getDimension() * 31 + Arrays.hashCode(this.getElements());
    }

    @Override
    public boolean equals(Object other) {
        if(this == other)return true;
        if(!(other instanceof Vector))return false;
        Vector vector = (Vector)other;
        if(this.getDimension() != vector.getDimension())return false;

        for(int i = 0; i < this.getDimension(); i++) {
            if(this.get(i).compareTo(vector.get(i)) != 0)return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return Arrays.toString(this.getElements());
    }

    public static class View extends Vector {
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
        public Vector set(int index, Rational value) {
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
