package model;

public class Rect {
	public Vector2 p1;
	public Vector2 p2;

	public Rect(Vector2 p1, Vector2 p2) {
		this.p1 = p1;
		this.p2 = p2;
	}

	public double distanceTo(Vector2 p) {
		double a = p2.y - p1.y;
		double b = p2.x - p1.x;
		return Math.abs(p.x * a - p.y * b + p2.x * p1.y - p2.y * p1.x) / Math.sqrt(a * a + b * b);
	}

	public boolean isBelow(Vector2 p) {
		double m = (p1.y - p2.y) / (p1.x - p2.x);
		double b = p1.y - m * p1.x;
		return p.y > m * p.x + b;
	}
}
