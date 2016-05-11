package generator;

import java.util.Locale;

public class StaticFileEntry {
	
	double mass;
	
	public StaticFileEntry(double mass) {
		this.mass = mass;
	}
	
	public String toString() {
		return String.format(Locale.US, "%1.4e", mass);
	}
	
}
