package kaptainwutax.mathutils.arithmetic;

import java.math.BigDecimal;
import java.math.BigInteger;

public class Complex {

    public static final Complex ZERO = Complex.of(Real.ZERO, Real.ZERO);
    public static final Complex ONE = Complex.of(Real.ONE, Real.ZERO);

    protected final Real real;
    protected final Real imaginary;

    protected Complex(Real real, Real imaginary) {
        this.real = real;
        this.imaginary = imaginary;
    }

    public Real getReal() {
        return this.real;
    }

    public Real getImaginary() {
        return this.imaginary;
    }

    public Complex negate() {
        return Complex.of(this.getReal().negate(), this.getImaginary().negate());
    }

    public Real magnitude() {
        return this.magnitudeSq().sqrt();
    }

    public Real magnitudeSq() {
        return this.getReal().multiply(this.getReal()).add(this.getImaginary().multiply(this.getImaginary()));
    }

    public Complex invert() {
        Real magnitude = this.magnitude();
        return Complex.of(this.getReal().divide(magnitude), this.getImaginary().divide(magnitude).negate());
    }

    public Complex conjugate() {
        return Complex.of(this.getReal(), this.getImaginary().negate());
    }

    public Complex add(Complex other) {
        return Complex.of(this.getReal().add(other.getReal()), this.getImaginary().add(other.getImaginary()));
    }

    public Complex subtract(Complex other) {
        return Complex.of(this.getReal().subtract(other.getReal()), this.getImaginary().subtract(other.getImaginary()));
    }

    public Complex multiply(Complex other) {
        Real a = this.getReal().multiply(other.getReal()).subtract(this.getImaginary().multiply(other.getImaginary()));
        Real b = this.getReal().multiply(other.getImaginary()).add(this.getImaginary().multiply(other.getReal()));
        return Complex.of(a, b);
    }

    public Complex multiply(Real other) {
        return Complex.of(this.getReal().multiply(other), this.getImaginary().multiply(other));
    }

    public Complex multiply(Rational other) {
        return Complex.of(this.getReal().multiply(other), this.getImaginary().multiply(other));
    }

    public Complex multiply(BigDecimal other) {
        return Complex.of(this.getReal().multiply(other), this.getImaginary().multiply(other));
    }

    public Complex multiply(BigInteger other) {
        return Complex.of(this.getReal().multiply(other), this.getImaginary().multiply(other));
    }

    public Complex multiply(double other) {
        return Complex.of(this.getReal().multiply(other), this.getImaginary().multiply(other));
    }

    public Complex multiply(long other) {
        return Complex.of(this.getReal().multiply(other), this.getImaginary().multiply(other));
    }

    public Complex divide(Complex other) {
        return this.multiply(other.invert());
    }

    public Complex divide(Real other) {
        return Complex.of(this.getReal().divide(other), this.getImaginary().divide(other));
    }

    public Complex divide(Rational other) {
        return Complex.of(this.getReal().divide(other), this.getImaginary().divide(other));
    }

    public Complex divide(BigDecimal other) {
        return Complex.of(this.getReal().divide(other), this.getImaginary().divide(other));
    }

    public Complex divide(BigInteger other) {
        return Complex.of(this.getReal().divide(other), this.getImaginary().divide(other));
    }

    public Complex divide(double other) {
        return Complex.of(this.getReal().divide(other), this.getImaginary().divide(other));
    }

    public Complex divide(long other) {
        return Complex.of(this.getReal().divide(other), this.getImaginary().divide(other));
    }

    @Override
    public int hashCode() {
        return this.getReal().hashCode() * 31 + this.getImaginary().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if(this == other)return true;
        if(!(other instanceof Complex))return false;
        Complex complex = (Complex)other;
        return this.getReal().compareTo(complex.getReal()) == 0
                && this.getImaginary().compareTo(complex.getImaginary()) == 0;
    }

    @Override
    public String toString() {
        if(this.getReal().signum() == 0 && this.getImaginary().signum() == 0)return "0";
        else if(this.getReal().signum() == 0)return this.getImaginary().toString() + "i";
        else if(this.getImaginary().signum() == 0)return this.getReal().toString();
        return this.getReal().toString() + (this.getImaginary().signum() > 0 ? " + " : " - ")
                + this.getImaginary().abs().toString() + "i";
    }

    public static Complex of(Real real, Real imaginary) {
        return new Complex(real, imaginary);
    }

    public static Complex of(BigDecimal real, BigDecimal imaginary) {
        return of(Real.of(real), Real.of(imaginary));
    }

    public static Complex of(BigInteger real, BigInteger imaginary) {
        return of(Real.of(real), Real.of(imaginary));
    }

    public static Complex of(double real, double imaginary) {
        return of(Real.of(real), Real.of(imaginary));
    }

    public static Complex of(long real, long imaginary) {
        return of(Real.of(real), Real.of(imaginary));
    }

    public static Complex of(Real real) {
        return of(real, Real.ZERO);
    }

    public static Complex of(BigDecimal value) {
        return of(Real.of(value));
    }

    public static Complex of(BigInteger value) {
        return of(Real.of(value));
    }

    public static Complex of(double value) {
        return of(Real.of(value));
    }

    public static Complex of(long value) {
        return of(Real.of(value));
    }

}
