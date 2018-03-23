package org.shrewsburyrobotics.pidlab;

/**
 * MotorModel simulates a motor as its input voltage is changed over time.
 */
public class MotorModel {
	private final double Kv;
	private final double T0;

	// Current speed in ticks/second.
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
	 * Adjust for friction be reducing the input value by 0.1 (closer to zero).
	 */
	private double adjustForFriction(double drive) {
		if (drive > 0) {
			return Math.max(0.0, drive - Constants.MOTOR_FRICTION);
		} else {
			return Math.min(0.0, drive + Constants.MOTOR_FRICTION);
		}
	}
	
	/**
	 * Step the motor forward by one time increment.
	 * 
	 * @param drive the input signal during this time increment, |drive| <= 1.0
	 */
	public void step(double drive) {
		// Cap the drive value to +- 1.0.
		drive = Math.min(drive,  1.0);
		drive = Math.max(drive, -1.0);

		drive = adjustForFriction(drive);
		
		// Update motor state.
		currentSpeed += Constants.STEP_TIME_SEC * (Kv * drive - currentSpeed) / T0;
		currentPosition += Constants.STEP_TIME_SEC * currentSpeed;
	}
	
	/**
	 * Get the current speed of the motor in ticks/second.
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
