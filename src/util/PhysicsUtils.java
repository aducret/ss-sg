package util;

import model.Particle;
import model.Vector2;

public class PhysicsUtils {
	public static final double G = 6.6738431e-11;

	/*
	 * gravitational force exerted by p2 on p1
	 */
	public static Vector2 gravitationalForce(Particle p2, Particle p1) {
		return p1.getPosition().substract(p2.getPosition()).normalize().scale(-G
				* ((p1.getMass() * p2.getMass()) / Math.pow((p2.getPosition().substract(p1.getPosition()).norm()), 2)));
	}

	public static double gravitationalFormceMagnitud(Particle p, Particle sun) {
		return (G * p.getMass() * sun.getMass()) / p.getPosition().distanceTo(sun.getPosition());
	}

	/*
	 * this is the velocity the planet needs to orbit the sun clockwise
	 */
	public static Vector2 orbitalVelocity(Particle planet, Particle sun) {
		return planet.getPosition().substract(sun.getPosition()).normalize().scale(orbitalSpeed(planet, sun))
				.rotateCW(Math.PI / 2.0);
	}

	/*
	 * this is the speed the planet needs to have a uniform circular movement
	 * around the sun
	 */
	public static double orbitalSpeed(Particle planet, Particle sun) {
		return Math.sqrt((G * Particle.MASS_SUN) / planet.getPosition().distanceTo(sun.getPosition()));
		// return Math.sqrt((gravitationalForce(sun, planet).norm() *
		// planet.getPosition().distanceTo(sun.getPosition())) /
		// planet.getMass());
	}
}
