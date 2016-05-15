package generator;

import java.util.Locale;

public class DynamicFileEntry {
	double x;
	double y;
	public DynamicFileEntry(double x, double y) {
		this.x = x;
		this.y = y;
	}
	public double getX() {
		return x;
	}
	public double getY() {
		return y;
	}
	
	public String toString() {
		return String.format(Locale.US, "%1.4e %1.4e", x, y);
	}
}
