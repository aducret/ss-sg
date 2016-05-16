package model;

import util.MatrixUtils;

public class Vector2 {

	private static double EPSILON = 1e-9;

	public double x;
	public double y;

	public Vector2(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public Vector2 sum(Vector2 vector) {
		return new Vector2(x + vector.x, y + vector.y);
	}

	public Vector2 substract(Vector2 vector) {
		return sum(vector.scale(-1));
	}

	public Vector2 scale(double l) {
		return new Vector2(x * l, y * l);
	}

	public double angleWith(Vector2 vector) {
		return Math.acos(dotProduct(vector) / (getMagnitude() * vector.getMagnitude()));
	}

	public double dotProduct(Vector2 vector) {
		return x * vector.x + y * vector.y;
	}

	public Vector2 rotateCCW(double angle) {
		return MatrixUtils.rotateCCW(this, angle);
	}

	public Vector2 rotateCW(double angle) {
		return MatrixUtils.rotateCW(this, angle);
	}

	public double distanceTo(Vector2 vector) {
		return Math.sqrt((x - vector.x) * (x - vector.x) + (y - vector.y) * (y - vector.y));
	}

	public double getMagnitude() {
		return Math.sqrt(x * x + y * y);
	}

	public Vector2 normalize() {
		return scale(1 / (norm()));
	}

	public double norm() {
		return Math.sqrt(x * x + y * y);
	}

	public double scalarProduct(Vector2 vector) {
		return x * vector.x + y * vector.y;
	}

	public String toString() {
		return "(" + checkToEpsilon(x) + ", " + checkToEpsilon(y) + ")";
	}

	public double checkToEpsilon(double x) {
		if (Math.abs(x) > EPSILON)
			return x;
		return 0;
	}
}
