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
	private double tao;
	private double vd;
	private double kn;
	private double kt;
	private double A;
	private double B;
	private Double maxRadius;

	private SimulationData() {
	}

	public void fixIds() {
		int id = 1;
		particlesMap.clear();
		for (Particle particle : particles) {
			particle.setId(id);
			particlesMap.put(id, particle);
			id++;
		}
	}

	public void removeParticleById(int id) {
		Particle particle = particlesMap.remove(id);
		if (particle == null)
			return;
		particles.remove(particle);
		particlesAmount--;
	}
	
	public double getMaxRadius() {
		if (maxRadius == null) {
			double max = 0;
			for (Particle particle: getParticles()) {
				if (particle.getRadius() > max) {
					max = particle.getRadius();
				}
			}
			maxRadius = max;
		}
		
		return maxRadius;
	}

	public Double getInteractionRadius() {
		return interactionRadius;
	}
	
	public void setInteractionRadius(double interactionRadius) {
		this.interactionRadius = interactionRadius;
	}

	public double getKineticEnergy() {
		double totalEnergy = 0;
		for (Particle particle : getParticles()) {
			totalEnergy += particle.getKineticEnergy();
		}
		return totalEnergy;
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

	public double getTao() {
		return tao;
	}

	public void setTao(double tao) {
		this.tao = tao;
	}

	public double getVd() {
		return vd;
	}

	public void setVd(double vd) {
		this.vd = vd;
	}

	public double getKn() {
		return kn;
	}

	public void setKn(double kn) {
		this.kn = kn;
	}

	public double getKt() {
		return kt;
	}

	public void setKt(double kt) {
		this.kt = kt;
	}

	public double getA() {
		return A;
	}

	public void setA(double a) {
		A = a;
	}

	public double getB() {
		return B;
	}

	public void setB(double b) {
		B = b;
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
		
		public Builder withVd(double vd) {
			simulationData.vd = vd;
			return this;
		}
		public Builder withKn(double kn) {
			simulationData.kn = kn;
			return this;
		}
		public Builder withKt(double kt) {
			simulationData.kt = kt;
			return this;
		}
		public Builder withA(double A) {
			simulationData.A = A;
			return this;
		}
		public Builder withB(double B) {
			simulationData.B = B;
			return this;
		}
		
		public Builder withTao(double tao) {
			simulationData.tao = tao;
			return this;
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
