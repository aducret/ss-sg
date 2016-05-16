package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SimulationData {
	private Double interactionRadius;
	private double width;
	private double height;
	private double L;
	private double W;
	private double D;
	private Integer particlesAmount;
	private List<Particle> particles;
	private HashMap<Integer, Particle> particlesMap;

	private SimulationData() {
	}
	
	public void fixIds() {
		int id = 1;
		particlesMap.clear();
		for (Particle particle: particles) {
			particle.setId(id);
			particlesMap.put(id, particle);
			id++;
		}
	}
	
	public void removeParticleById(int id) {
		Particle particle = particlesMap.remove(id);
		if (particle == null) return;
		particles.remove(particle);
		particlesAmount--;
	}

	public Double getInteractionRadius() {
		return interactionRadius;
	}

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}

	public double getL() {
		return L;
	}

	public double getW() {
		return W;
	}

	public double getD() {
		return D;
	}

	public Integer getParticlesAmount() {
		return particlesAmount;
	}
	
	public List<Particle> getParticles() {
		return particles;
	}
	
	public Particle getParticleById(int id) {
		return particlesMap.get(id);
	}
	
	public void setParticles(List<Particle> particles) {
		this.particles = particles;
	}
	
	public static class Builder {
		private SimulationData simulationData;

		private Builder() {
			simulationData = new SimulationData();
			simulationData.particles = new ArrayList<>();
			simulationData.particlesMap = new HashMap<>();
		}

		public static Builder create() {
			return new Builder();
		}

		public Builder withInteractionRadius(double interactionRadius) {
			simulationData.interactionRadius = interactionRadius;
			return this;
		}

		public Builder withParticlesAmount(int particlesAmount) {
			this.simulationData.particlesAmount = particlesAmount;
			return this;
		}

		public Builder withSpaceDimension(double width, double height) {
			this.simulationData.width = width;
			this.simulationData.height = height;
			return this;
		}
		
		public Builder withParameters(double L, double W, double D) {
			this.simulationData.L = L;
			this.simulationData.W = W;
			this.simulationData.D = D;
			return this;
		}

		public Builder withParticle(Particle particle) {
			simulationData.particles.add(particle);
			simulationData.particlesMap.put(particle.getId(), particle);
			return this;
		}
		
		public SimulationData build() {
			return simulationData;
		}
	}
}
