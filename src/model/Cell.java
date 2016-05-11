package model;

import java.util.HashSet;
import java.util.Set;

public class Cell {
	private Set<Particle> particles;
	
	public Cell() {
		this.particles = new HashSet<Particle>();
	}
	
	public Set<Particle> getParticles() {
		return particles;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (Particle particle: particles) {
			builder.append(particle + ", ");
		}
		return builder.toString();
	}
}
