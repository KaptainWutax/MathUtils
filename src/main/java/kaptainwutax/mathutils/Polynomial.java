package kaptainwutax.mathutils;

import java.util.Arrays;

public final class Polynomial {

	protected final Rational[] coefficients;
	protected int degree = -1;

	protected Polynomial derivative;

	public Polynomial(Rational... coefficients) {
		this.coefficients = coefficients;
		this.computeDegree();
	}

	public Polynomial(Rational coefficient, int exponent) {
		this.coefficients = new Rational[exponent + 1];
		this.coefficients[exponent] = coefficient;
		this.degree = exponent;
	}

	protected void computeDegree() {
		for(int i = this.coefficients.length - 1; i >= 0; i--) {
			if(this.coefficients[i].signum() != 0) {
				this.degree = i;
				break;
			}
		}
	}

	public int getDegree() {
		return this.degree;
	}

	public Rational getCoefficient(int exponent) {
		return this.coefficients[exponent];
	}

	public Rational[] getCoefficients() {
		return Arrays.copyOf(this.coefficients, this.coefficients.length);
	}

	public Rational evaluate(Rational point) {
		Rational result = Rational.ZERO;

		for(int i = this.degree; i >= 0; i--) {
			result = result.multiply(point).add(this.coefficients[i]);
		}

		return result;
	}

	public Polynomial differentiate() {
		if(this.derivative != null)return this.derivative;
		if(this.degree <= 0)return new Polynomial(Rational.ZERO, 0);

		Polynomial r = new Polynomial(Rational.ZERO, this.degree - 1);

		for(int e = 1; e < this.degree; e++) {
			r.coefficients[e - 1] =  this.coefficients[e].multiply(e);
		}

		return this.derivative = r;
	}

	public Polynomial add(Polynomial other) {
		Polynomial r = new Polynomial(Rational.ZERO, Math.max(this.degree, other.degree));

		for (int i = 0; i <= r.degree; i++) {
			if(i <= this.degree) {
				r.coefficients[i] = this.coefficients[i];
			}

			if(i <= other.degree) {
				r.coefficients[i] = r.coefficients[i].add(other.coefficients[i]);
			}
		}

		r.computeDegree();
		return r;
	}

	public Polynomial multiply(Polynomial other) {
		Polynomial r = new Polynomial(Rational.ZERO, this.degree + other.degree);

		for(int i = 0; i <= this.degree; i++) {
			for(int j = 0; j <= other.degree; j++) {
				r.coefficients[i + j] = r.coefficients[i + j].add(this.coefficients[i].multiply(other.coefficients[j]));
			}
		}

		r.computeDegree();
		return r;
	}

	public Polynomial compose(Polynomial other) {
		Polynomial r = new Polynomial(Rational.ZERO, 0);

		for(int i = this.degree; i >= 0; i--) {
			Polynomial t = new Polynomial(this.coefficients[i], 0);
			r = t.add(other.multiply(r));
		}

		return r;
	}

	@Override
	public int hashCode() {
		return 31 * this.degree + Arrays.hashCode(this.coefficients);
	}

	@Override
	public boolean equals(Object other) {
		if(this == other)return true;
		if(!(other instanceof Polynomial))return false;
		Polynomial polynomial = (Polynomial)other;
		if(this.degree != polynomial.degree)return false;

		for(int i = 0; i < this.getDegree(); i++) {
			if(this.getCoefficient(i).compareTo(polynomial.getCoefficient(i)) != 0)return false;
		}

		return true;
	}

	@Override
	public String toString() {
		if(this.degree < 0) {
			return "0";
		}

		StringBuilder sb = new StringBuilder();

		for(int i = this.degree; i >= 0; i--) {
			Rational c = this.coefficients[i];
			int sign = c.signum();
			c = c.multiply(sign);

			if(i != this.degree) {
				sb.append(sign == 1 ? " + " : " - ");
			}

			sb.append(c);

			if(i != 0) {
				sb.append("x");
				if(i != 1)sb.append("^").append(i);
			}
		}

		return sb.toString();
	}

}
