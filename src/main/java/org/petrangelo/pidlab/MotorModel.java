package org.petrangelo.pidlab;

/**
 * MotorModel simulates a motor as its input voltage is changed over time.
 */
public class MotorModel {
	public final static double STEP_TIME_SEC = 0.020;

	private final double Kv;
	private final double T0;

	// Current speed in ticks/sec.
	private double currentSpeed = 0.0;

	// Current position in ticks.
	private double currentPosition = 0.0;
	
	/**
	 * Create a MotorModel object.
	 * 
	 * @param Kv motor gain, or the maximum speed of the motor at full input voltage, in ticks/second
	 * @param T0 time constant for the motor, or the time it takes for the motor to
	 * 		  reach ~62% of the final speed at a given input voltage, in seconds
	 */
	public MotorModel(double Kv, double T0) {
		this.Kv = Kv;
		this.T0 = T0;
	}

	/**
	 * Step the motor forward by one time increment.
	 * @param Vin the input voltage during this time increment
	 */
	public void step(double Vin) {
		currentSpeed += STEP_TIME_SEC * (Kv * Vin - currentSpeed) / T0;
		currentPosition += STEP_TIME_SEC * currentSpeed;
	}
	
	/**
	 * Get the current speed of the motor in ticks/sec.
	 */
	public double getSpeed() {
		return currentSpeed;
	}

	/**
	 * Get the current position of the motor in ticks.
	 */
	public double getPosition() {
		return currentPosition;
	}
}
