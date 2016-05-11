package util;

public class RandomUtils {
	public static double randomBetween(double a, double b) {
		return Math.random()*(b - a) + a;
	}
}
