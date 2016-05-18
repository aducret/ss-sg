package integration;

import model.Particle;
import model.Vector2;

public class VerletAlgorithm {

	public static void move(double dt, Particle p) {
		Vector2 nextPosition = nextPosition(dt, p);
		Vector2 oldVelocity = getVelocity(dt, p.oldPosition, nextPosition);
		p.oldPosition = p.getPosition();
		p.oldVelocity = oldVelocity;
		p.setPosition(nextPosition);
		p.setVelocity(getVelocity(dt, p));
	}

	/*
	 * TODO: check if force can be used for oldPosition too.
	 */
	public static Vector2 nextPosition(double dt, Particle p) {
		Vector2 oldPosition = p.oldPosition == null ? EulerAlgorithm.nextPosition(-dt, p) : p.oldPosition;
		return p.getPosition().scale(2).substract(oldPosition).sum(p.resultantForce.scale((dt * dt) / (p.getMass())));
	}

	private static Vector2 getVelocity(double dt, Vector2 oldPosition, Vector2 newPosition) {
		if (oldPosition == null || newPosition == null)
			return null;
		return newPosition.substract(oldPosition).scale(1 / (2 * dt));
	}

	public static Vector2 getVelocity(double dt, Particle particle) {
		if (particle.oldPosition == null)
			return new Vector2(0, 0);
		Vector2 ans = getVelocity(dt, particle.oldPosition, nextPosition(dt, particle));
		particle.setVelocity(ans);
		return ans;
	}
}
