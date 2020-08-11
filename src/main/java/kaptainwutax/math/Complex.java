package kaptainwutax.math;

public class Complex {

    protected final Rational real;
    protected final Rational imaginary;

    public Complex(Rational real, Rational imaginary) {
        this.real = real;
        this.imaginary = imaginary;
    }

    public Rational getReal() {
        return this.real;
    }

    public Rational getImaginary() {
        return this.imaginary;
    }

    public Rational absSq() {
        return this.getReal().shiftLeft(1).add(this.getImaginary().shiftLeft(1));
    }

    public Complex inverse() {
        Rational magnitude = this.absSq();
        return new Complex(this.getReal().divide(magnitude), this.getImaginary().divide(magnitude).negate());
    }

    public Complex add(Complex other) {
        return new Complex(this.getReal().add(other.getReal()), this.getImaginary().add(other.getImaginary()));
    }

    public Complex subtract(Complex other) {
        return new Complex(this.getReal().subtract(other.getReal()), this.getImaginary().subtract(other.getImaginary()));
    }

    public Complex multiply(Complex other) {
        Rational a = this.getReal().multiply(other.getReal()).subtract(this.getImaginary().multiply(other.getImaginary()));
        Rational b = this.getReal().multiply(other.getImaginary()).add(this.getImaginary().multiply(other.getReal()));
        return new Complex(a, b);
    }

    public Complex divide(Complex other) {
        return this.multiply(other.inverse());
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
        if(this.getReal().signum() == 0)return this.getImaginary().toString() + "i";
        else if(this.getImaginary().signum() == 0)return this.getReal().toString();
        return this.getReal().toString() + (this.getImaginary().signum() > 0 ? " + " : " - ")
                + this.getImaginary().abs().toString() + "i";
    }

}
