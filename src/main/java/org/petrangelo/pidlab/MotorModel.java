package org.petrangelo.pidlab;

public class MotorModel {
	private final double Kv;
	private final double T0;
	private double currentSpeed;
	
	public MotorModel(double Kv, double T0) {
		this.Kv = Kv;
		this.T0 = T0;
	}
	
	public double step(double Vin) {
		double newSpeed = currentSpeed + (Kv * Vin - currentSpeed) / T0;
		currentSpeed = newSpeed;
		return currentSpeed;
	}
	
	public static void main(String[] args) {
		MotorModel model = new MotorModel(100, 20);
		for (int i = 0; i < 100; i++) {
			System.out.println(i + "," + model.step(1));
		}
	}
}
