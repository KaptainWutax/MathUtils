package kaptainwutax.mathutils.arithmetic;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

public class Rational extends Number implements Comparable<Rational> {

    protected static final BigInteger THRESHOLD = BigInteger.ONE.shiftLeft(128);

    public static final Rational ZERO = Rational.of(0, 1);
    public static final Rational HALF = Rational.of(1, 2);
    public static final Rational ONE = Rational.of(1, 1);

    protected BigInteger numerator;
    protected BigInteger denominator;

    protected Rational(BigInteger numerator, BigInteger denominator) {
        if(denominator.signum() == 0) {
            throw new ArithmeticException("/ by zero");
        }

        this.numerator = numerator;
        this.denominator = denominator;
        this.simplify();
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

    protected Rational reduce() {
        BigInteger gcd = this.numerator.gcd(this.denominator);
        this.numerator = this.numerator.divide(gcd);
        this.denominator = this.denominator.divide(gcd);
        return this;
    }

    public Rational abs() {
        return this.getNumerator().signum() < 0 ? this.negate() : this;
    }
    
    public Rational negate() {
        return Rational.of(this.getNumerator().negate(), this.getDenominator());
    }
    
    public Rational invert() {
        return Rational.of(this.getDenominator(), this.getNumerator());
    }

    public int signum() {
        return this.getNumerator().signum();
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
        return Rational.of(a.add(b), this.getDenominator().multiply(addend.getDenominator()));
    }
    
    public Rational add(BigDecimal addend) {
        return this.add(Rational.of(addend));
    }

    public Rational add(BigInteger addend) {
        return Rational.of(this.getNumerator().add(addend.multiply(this.getDenominator())), this.getDenominator());
    }
    
    public Rational add(double addend) {
        return this.add(Rational.of(addend));
    }
    
    public Rational add(long addend) {
        return this.add(BigInteger.valueOf(addend));
    }

    public Rational subtract(Rational subtrahend) {
        return this.add(subtrahend.negate());
    }
    
    public Rational subtract(BigDecimal subtrahend) {
        return this.subtract(Rational.of(subtrahend));
    }

    public Rational subtract(BigInteger subtrahend) {
        return this.add(subtrahend.negate());
    }
    
    public Rational subtract(double subtrahend) {
        return this.subtract(Rational.of(subtrahend));
    }

    public Rational subtract(long subtrahend) {
        return this.subtract(BigInteger.valueOf(subtrahend));
    }

    public Rational multiply(Rational multiplier) {
        BigInteger a = this.getNumerator().multiply(multiplier.getNumerator());
        BigInteger b = this.getDenominator().multiply(multiplier.getDenominator());
        return Rational.of(a, b);
    }

    public Rational multiply(BigDecimal multiplier) {
        return this.multiply(Rational.of(multiplier));
    }

    public Rational multiply(BigInteger multiplier) {
        return Rational.of(this.getNumerator().multiply(multiplier), this.getDenominator());
    }

    public Rational multiply(double multiplier) {
        return this.multiply(Rational.of(multiplier));
    }
    
    public Rational multiply(long multiplier) {
        return this.multiply(BigInteger.valueOf(multiplier));
    }

    public Rational divide(Rational divisor) {
        return this.multiply(divisor.invert());
    }

    public Rational divide(BigDecimal divisor) {
        return this.divide(Rational.of(divisor));
    }

    public Rational divide(BigInteger divisor) {
        return Rational.of(this.getNumerator(), this.getDenominator().multiply(divisor));
    }
    
    public Rational divide(double divisor) {
        return this.divide(Rational.of(divisor));
    }
    
    public Rational divide(long divisor) {
        return this.divide(BigInteger.valueOf(divisor));
    }
    
    public Rational pow(BigInteger exponent) {
        return this.pow(exponent.intValueExact());
    }
    
    public Rational pow(int exponent) {
        BigInteger a = this.getNumerator().pow(exponent);
        BigInteger b = this.getDenominator().pow(exponent);
        return Rational.of(a, b);
    }

    public Rational shiftRight(int n) {
        Rational r = this;
        int i = Math.min(this.getNumerator().getLowestSetBit(), n);
        if(i > 0)r = Rational.of(this.getNumerator().shiftRight(i), this.getDenominator());
        if(n - i > 0)r = Rational.of(r.getNumerator(), r.getDenominator().shiftLeft(n - i));
        return r;
    }

    public Rational shiftLeft(int n) {
        Rational r = this;
        int i = Math.min(this.getDenominator().getLowestSetBit(), n);
        if(i > 0)r = Rational.of(this.getNumerator(), this.getDenominator().shiftRight(i));
        if(n - i > 0)r = Rational.of(r.getNumerator().shiftLeft(n - i), r.getDenominator());
        return r;
    }

    public Rational floor() {
        if(this.getDenominator().equals(BigInteger.ONE))return this;
        BigInteger a = this.getNumerator().divide(this.getDenominator());
        if(this.getNumerator().signum() < 0)a = a.subtract(BigInteger.ONE);
        return Rational.of(a);
    }

    public Rational ceil() {
        if(this.getDenominator().equals(BigInteger.ONE))return this;
        BigInteger a = this.getNumerator().divide(this.getDenominator());
        if(this.getNumerator().signum() < 0)a = a.add(BigInteger.ONE);
        return Rational.of(a);
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

    public BigDecimal toBigDecimal(int scale, RoundingMode roundingMode) {
        return new BigDecimal(this.getNumerator()).setScale(scale, roundingMode)
                .divide(new BigDecimal(this.getDenominator()), roundingMode);
    }

    public Real toReal(int scale, RoundingMode roundingMode) {
        return Real.of(this.toBigDecimal(scale, roundingMode));
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
        return this.compareTo((Rational)other) == 0;
    }

    @Override
    public String toString() {
        Rational r = this.reduce();
        return r.signum() == 0 || r.getDenominator().equals(BigInteger.ONE)
                ? r.getNumerator().toString() : r.getNumerator() + " / " + r.getDenominator();
    }

    public String toString(int scale) {
        return this.toBigDecimal(scale, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
    }

    public static Rational of(BigInteger numerator, BigInteger denominator) {
        return new Rational(numerator, denominator);
    }

    public static Rational of(long numerator, long denominator) {
        return of(BigInteger.valueOf(numerator), BigInteger.valueOf(denominator));
    }

    public static Rational of(BigInteger numerator, long denominator) {
        return of(numerator, BigInteger.valueOf(denominator));
    }

    public static Rational of(long numerator, BigInteger denominator) {
        return of(BigInteger.valueOf(numerator), denominator);
    }

    public static Rational of(BigDecimal value) {
        value = value.stripTrailingZeros();
        return of(value.movePointRight(value.scale()).toBigIntegerExact(), BigInteger.TEN.pow(value.scale()));
    }

    public static Rational of(BigInteger value) {
        return of(value, BigInteger.ONE);
    }

    public static Rational of(double value) {
        return of(BigDecimal.valueOf(value));
    }

    public static Rational of(long value) {
        return of(value, 1);
    }

}
