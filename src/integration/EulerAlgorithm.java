package integration;

import model.Particle;
import model.Vector2;
import util.PhysicsUtils;

public class EulerAlgorithm {

	public static void move(double dt, Particle p, Particle sun, Vector2 force) {
		Vector2 nextPosition = nextPosition(dt, p, sun, force);
		Vector2 nextVelocity = nextVelocity(dt, p, sun, force);
		p.setPosition(nextPosition);
		p.setVelocity(nextVelocity);
	}

	public static Vector2 nextPosition(double dt, Particle p, Particle sun, Vector2 force) {
		return p.getPosition().sum(sun.getPosition()).sum(p.getVelocity().scale(dt)).sum(force);
	}

	public static Vector2 nextVelocity(double dt, Particle p, Particle sun, Vector2 force) {
		return p.getVelocity().sum(force);
	}

}
