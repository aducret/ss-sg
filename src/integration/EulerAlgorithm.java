package integration;

import model.Particle;
import model.Vector2;
import util.PhysicsUtils;

public class EulerAlgorithm {

	public static void move(double dt, Particle p) {
		Vector2 nextPosition = nextPosition(dt, p);
		Vector2 nextVelocity = nextVelocity(dt, p);
		p.setPosition(nextPosition);
		p.setVelocity(nextVelocity);
	}

	public static Vector2 nextPosition(double dt, Particle p) {
		return p.getPosition().sum(p.getVelocity().scale(dt)).sum(p.resultantForce.scale((dt * dt) / (2 * p.getMass())));
	}

	public static Vector2 nextVelocity(double dt, Particle p) {
		return p.getVelocity().sum(p.resultantForce.scale(dt / p.getMass()));
	}

}
