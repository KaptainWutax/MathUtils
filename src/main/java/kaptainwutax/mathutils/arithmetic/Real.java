package kaptainwutax.mathutils.arithmetic;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public class Real extends Number implements Comparable<Real> {

    public static final Real ZERO = Real.of(0.0D);
    public static final Real HALF = Real.of(0.5D);
    public static final Real ONE = Real.of(1.0D);
    public static final Real TWO = Real.of(2.0D);

    protected BigDecimal value;

    protected Real(BigDecimal value) {
        this.value = value;
    }

    public static Real of(BigDecimal value) {
        return new Real(value);
    }

    public static Real of(BigInteger value) {
        return of(new BigDecimal(value).setScale(10, RoundingMode.HALF_UP));
    }

    public static Real of(double value) {
        return of(BigDecimal.valueOf(value).setScale(10, RoundingMode.HALF_UP));
    }

    public static Real of(long value) {
        return of(BigDecimal.valueOf(value).setScale(10, RoundingMode.HALF_UP));
    }

    public BigDecimal getValue() {
        return this.value;
    }

    public int getScale() {
        return this.getValue().scale();
    }

    private Real setScale(int scale) {
        return this.setScale(scale, RoundingMode.HALF_UP);
    }

    public Real setScale(int scale, RoundingMode rounding) {
        return Real.of(this.getValue().setScale(scale, rounding));
    }

    public Real strip() {
        return Real.of(this.getValue().stripTrailingZeros());
    }

    public Real abs() {
        return Real.of(this.getValue().abs());
    }
    
    public Real negate() {
        return Real.of(this.getValue().negate());
    }
    
    public Real invert() {
        return Real.of(BigDecimal.ONE.divide(this.getValue(), this.getValue().scale(), RoundingMode.HALF_UP));
    }

    public int signum() {
        return this.getValue().signum();
    }
    
    public Real min(Real other) {
        return this.compareTo(other) <= 0 ? this : other;
    }

    public Real max(Real other) {
        return this.compareTo(other) >= 0 ? this : other;
    }

    public Real add(Real addend) {
        return this.add(addend.getValue());
    }

    public Real add(Rational addend) {
        return this.add(addend.toBigDecimal(this.getScale(), RoundingMode.HALF_UP));
    }

    public Real add(BigDecimal addend) {
        return Real.of(this.getValue().add(addend));
    }

    public Real add(BigInteger addend) {
        return this.add(new BigDecimal(addend));
    }
    
    public Real add(double addend) {
        return this.add(BigDecimal.valueOf(addend));
    }
    
    public Real add(long addend) {
        return this.add(BigDecimal.valueOf(addend));
    }

    public Real subtract(Real subtrahend) {
        return this.subtract(subtrahend.getValue());
    }

    public Real subtract(Rational subtrahend) {
        return this.subtract(subtrahend.toBigDecimal(this.getScale(), RoundingMode.HALF_UP));
    }

    public Real subtract(BigDecimal subtrahend) {
        return Real.of(this.getValue().subtract(subtrahend));
    }

    public Real subtract(BigInteger subtrahend) {
        return this.subtract(new BigDecimal(subtrahend));
    }
    
    public Real subtract(double subtrahend) {
        return this.subtract(BigDecimal.valueOf(subtrahend));
    }

    public Real subtract(long subtrahend) {
        return this.subtract(BigDecimal.valueOf(subtrahend));
    }

    public Real multiply(Real multiplier) {
        return this.multiply(multiplier.getValue());
    }

    public Real multiply(Rational multiplier) {
        return this.multiply(multiplier.toBigDecimal(this.getScale(), RoundingMode.HALF_UP));
    }

    public Real multiply(BigDecimal multiplier) {
        return Real.of(this.getValue().multiply(multiplier));
    }

    public Real multiply(BigInteger multiplier) {
        return this.multiply(new BigDecimal(multiplier));
    }

    public Real multiply(double multiplier) {
        return this.multiply(BigDecimal.valueOf(multiplier));
    }
    
    public Real multiply(long multiplier) {
        return this.multiply(BigDecimal.valueOf(multiplier));
    }

    public Real divide(Real divisor) {
        return this.divide(divisor.getValue());
    }

    public Real divide(Rational divisor) {
        return this.divide(divisor.toBigDecimal(this.getScale(), RoundingMode.HALF_UP));
    }

    public Real divide(BigDecimal divisor) {
        return Real.of(this.getValue().divide(divisor, RoundingMode.HALF_UP));
    }

    public Real divide(BigInteger divisor) {
        return this.divide(new BigDecimal(divisor));
    }
    
    public Real divide(double divisor) {
        return this.divide(BigDecimal.valueOf(divisor));
    }
    
    public Real divide(long divisor) {
        return this.divide(BigDecimal.valueOf(divisor));
    }

    public Real pow(Rational exponent) {
        return this.pow(exponent.getNumerator()).nthRoot(exponent.getDenominator());
    }

    public Real pow(BigInteger exponent) {
        return this.pow(exponent.intValueExact());
    }

    public Real pow(int exponent) {
        return Real.of(this.getValue().pow(exponent));
    }

    public Real nthRoot(BigInteger n) {
        return this.nthRoot(n.intValue());
    }

    public Real nthRoot(int n) {
        if(n <= 0) {
            throw new IllegalArgumentException("Root must be positive");
        } else if(this.compareTo(Real.ZERO) < 0) {
            throw new IllegalArgumentException("Root of negative number");
        } else if(this.equals(Real.ZERO)) {
            return Real.ZERO;
        } else if(n == 1) {
            return this;
        }

        Real xPrev = this;
        Real x = this.divide(n);
        Real a = Real.of(n - 1), p = Real.of(BigDecimal.ONE.movePointLeft(this.getScale()));

        while(x.subtract(xPrev).abs().compareTo(p) > 0) {
            xPrev = x;
            x = a.multiply(x).add(this.divide(x.pow(n - 1))).divide(n);
        }

        return x.setScale(this.getScale(), RoundingMode.HALF_UP);
    }

    public Real sqrt() {
        return this.nthRoot(2);
    }

    public Real cbrt() {
        return this.nthRoot(3);
    }

    public Real floor() {
        return this.setScale(0, RoundingMode.FLOOR);
    }

    public Real ceil() {
        return this.setScale(0, RoundingMode.CEILING);
    }

    public Real round() {
        return this.setScale(0, RoundingMode.HALF_UP);
    }

    @Override
    public int intValue() {
        return this.getValue().intValue();
    }

    @Override
    public long longValue() {
        return this.getValue().longValue();
    }

    @Override
    public float floatValue() {
        return this.getValue().floatValue();
    }

    @Override
    public double doubleValue() {
        return this.getValue().doubleValue();
    }

    public BigInteger toBigInteger() {
        return this.getValue().toBigInteger();
    }

    public BigDecimal toBigDecimal() {
        return this.getValue();
    }

    public Rational toRational() {
        return Rational.of(this.getValue());
    }

    @Override
    public int compareTo(Real other) {
        return this.getValue().compareTo(other.getValue());
    }

    @Override
    public int hashCode() {
        return this.getValue().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return this.getValue().equals(other);
    }

    @Override
    public String toString() {
        return this.getValue().stripTrailingZeros().toString();
    }

}
