package model;

public class Collision {
	private Particle p1;
	private Particle p2;
	
	public Collision(Particle p1, Particle p2) {
		this.p1 = p1;
		this.p2 = p2;
	}

	public Particle getP1() {
		return p1;
	}

	public Particle getP2() {
		return p2;
	}
}
