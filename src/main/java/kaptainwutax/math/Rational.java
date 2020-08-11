package kaptainwutax.math;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

public class Rational extends Number implements Comparable<Rational> {

    public static final BigInteger THRESHOLD = BigInteger.ONE.shiftLeft(128);

    public static final Rational ZERO = new Rational(0, 1);
    public static final Rational HALF = new Rational(1, 2);
    public static final Rational ONE = new Rational(1, 1);

    protected BigInteger numerator;
    protected BigInteger denominator;

    public Rational(BigInteger numerator, BigInteger denominator) {
        if(denominator.signum() == 0) {
            throw new ArithmeticException("/ by zero");
        }

        this.numerator = numerator;
        this.denominator = denominator;
        this.simplify();
    }

    public Rational(long numerator, long denominator) {
        this(BigInteger.valueOf(numerator), BigInteger.valueOf(denominator));
    }

    public Rational(BigInteger value) {
        this(value, BigInteger.ONE);
    }

    public Rational(long value) {
        this(value, 1);
    }

    public BigInteger getNumerator() {
        return this.numerator;
    }

    public BigInteger getDenominator() {
        return this.denominator;
    }

    protected Rational simplify() {
        if(this.numerator.signum() == 0) {
            this.denominator = BigInteger.ONE;
            return this;
        } else if(this.denominator.signum() < 0) {
            this.numerator = this.numerator.negate();
            this.denominator = this.denominator.negate();
        }

        if(this.numerator.compareTo(THRESHOLD) < 0
                && this.denominator.compareTo(THRESHOLD) < 0)return this;

        return this.reduce();
    }

    public Rational reduce() {
        BigInteger gcd = this.numerator.gcd(this.denominator);
        this.numerator = this.numerator.divide(gcd);
        this.denominator = this.denominator.divide(gcd);
        return this;
    }

    public Rational abs() {
        return this.getNumerator().signum() < 0 ? this.negate() : this;
    }

    public Rational negate() {
        return new Rational(this.getNumerator().negate(), this.getDenominator());
    }

    public Rational invert() {
        return new Rational(this.getDenominator(), this.getNumerator());
    }

    public Rational min(Rational other) {
        return this.compareTo(other) <= 0 ? this : other;
    }

    public Rational max(Rational other) {
        return this.compareTo(other) >= 0 ? this : other;
    }

    public Rational add(Rational addend) {
        BigInteger a = this.getNumerator().multiply(addend.getDenominator());
        BigInteger b = addend.getNumerator().multiply(this.getDenominator());
        return new Rational(a.add(b), this.getDenominator().multiply(addend.getDenominator()));
    }

    public Rational add(BigInteger addend) {
        return new Rational(this.getNumerator().add(addend.multiply(this.getDenominator())), this.getDenominator());
    }

    public Rational add(long addend) {
        return this.add(BigInteger.valueOf(addend));
    }

    public Rational subtract(Rational subtrahend) {
        return this.add(subtrahend.negate());
    }

    public Rational subtract(BigInteger subtrahend) {
        return this.add(subtrahend.negate());
    }

    public Rational subtract(long subtrahend) {
        return this.subtract(BigInteger.valueOf(subtrahend));
    }

    public Rational multiply(Rational factor) {
        BigInteger a = this.getNumerator().multiply(factor.getNumerator());
        BigInteger b = this.getDenominator().multiply(factor.getDenominator());
        return new Rational(a, b);
    }

    public Rational multiply(BigInteger factor) {
        return new Rational(this.getNumerator().multiply(factor), this.getDenominator());
    }

    public Rational multiply(long factor) {
        return this.multiply(BigInteger.valueOf(factor));
    }

    public Rational divide(Rational divisor) {
        return this.multiply(divisor.invert());
    }

    public Rational divide(BigInteger divisor) {
        return new Rational(this.getNumerator(), this.getDenominator().multiply(divisor));
    }

    public Rational divide(long divisor) {
        return this.divide(BigInteger.valueOf(divisor));
    }

    public Rational pow(long exponent) {
        BigInteger a = this.getNumerator().pow(Math.toIntExact(exponent));
        BigInteger b = this.getDenominator().pow(Math.toIntExact(exponent));
        return new Rational(a, b);
    }

    public Rational pow(BigInteger exponent) {
        return this.pow(exponent.longValueExact());
    }

    public Rational floor() {
        if(this.getDenominator().equals(BigInteger.ONE))return this;
        BigInteger a = this.getNumerator().divide(this.getDenominator());
        if(this.getNumerator().signum() < 0)a = a.subtract(BigInteger.ONE);
        return new Rational(a);
    }

    public Rational ceil() {
        if(this.getDenominator().equals(BigInteger.ONE))return this;
        BigInteger a = this.getNumerator().divide(this.getDenominator());
        if(this.getNumerator().signum() < 0)a = a.add(BigInteger.ONE);
        return new Rational(a);
    }

    public Rational round() {
        return this.add(HALF).floor();
    }

    @Override
    public int intValue() {
        return this.getNumerator().divide(this.getDenominator()).intValue();
    }

    @Override
    public long longValue() {
        return this.getNumerator().divide(this.getDenominator()).longValue();
    }

    @Override
    public float floatValue() {
        BigDecimal a = new BigDecimal(this.getNumerator());
        BigDecimal b = new BigDecimal(this.getDenominator());
        return a.divide(b, MathContext.DECIMAL32).floatValue();
    }

    @Override
    public double doubleValue() {
        BigDecimal a = new BigDecimal(this.getNumerator());
        BigDecimal b = new BigDecimal(this.getDenominator());
        return a.divide(b, MathContext.DECIMAL64).doubleValue();
    }

    public BigInteger toBigInteger() {
        return this.getNumerator().divide(this.getDenominator());
    }

    public BigDecimal toBigDecimal() {
        return this.toBigDecimal(4, RoundingMode.HALF_UP);
    }

    public BigDecimal toBigDecimal(int scale, RoundingMode roundingMode) {
        return new BigDecimal(this.getNumerator()).setScale(scale, roundingMode)
                .divide(new BigDecimal(this.getDenominator()), roundingMode);
    }

    public BigDecimal toBigDecimal(int scale, MathContext mathContext) {
        return new BigDecimal(this.getNumerator()).setScale(scale, RoundingMode.HALF_UP)
                .divide(new BigDecimal(this.getDenominator()), mathContext);
    }

    public static Rational min(Rational... values) {
        Rational min = values[0];

        for(int i = 1; i < values.length; i++) {
            min = min.min(values[i]);
        }

        return min;
    }

    public static Rational max(Rational... values) {
        Rational max = values[0];

        for(int i = 1; i < values.length; i++) {
            max = max.max(values[i]);
        }

        return max;
    }

    @Override
    public int compareTo(Rational other) {
        BigInteger a = this.getNumerator().multiply(other.getDenominator());
        BigInteger b = this.getDenominator().multiply(other.getNumerator());
        return a.compareTo(b);
    }

    @Override
    public int hashCode() {
        return this.getNumerator().hashCode() + 31 * this.getDenominator().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if(other == this)return true;
        if(!(other instanceof Rational))return false;
        Rational r = ((Rational)other).reduce();
        this.reduce();
        return this.getNumerator().equals(r.getNumerator())
                && this.getDenominator().equals(r.getDenominator());
    }

    @Override
    public String toString() {
        return this.toBigDecimal().toString();
    }

}
