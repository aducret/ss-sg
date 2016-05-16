package generator;

import java.util.Locale;

public class StaticFileEntry {
	
	double mass;
	double radius;
	
	public StaticFileEntry(double mass, double radius) {
		this.mass = mass;
		this.radius = radius;
	}
	
	public String toString() {
		return String.format(Locale.US, "%1.4e %1.4e", mass, radius);
	}
	
}
