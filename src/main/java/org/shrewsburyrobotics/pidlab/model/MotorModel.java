package org.shrewsburyrobotics.pidlab.model;

import java.util.Collections;
import java.util.LinkedList;

/**
 * MotorModel simulates a motor as its input signal is changed over time.
 */
public class MotorModel {
	private final double gain; // Kp
	private final double timeConstant; // Tp
	private final double deadTime; // theta P

	private final LinkedList<Double> driveMemory;

	// Current acceleration in ticks/second^2.
	private double currentAcceleration = 0.0;

	// Current speed in ticks/second.
	private double currentSpeed = 0.0;

	// Current position in ticks.
	private double currentPosition = 0.0;

	/**
	 * Create a MotorModel object.
	 * 
	 * @param gain motor gain (Kp), or the maximum speed of the motor at full power, in ticks/second
     * @param timeConstant time constant (Tp) for the motor, or the time it takes for the motor to
     *        reach ~62% of the final speed at a given input, in seconds
     * @param deadTime dead time (theta P) is the time between a change in input and when a measurable
     *        response occurs, in seconds
	 */
	public MotorModel(double gain, double timeConstant, double deadTime) {
		this.gain = gain;
		this.timeConstant = timeConstant;
		this.deadTime = deadTime;
		int numDeadTimeTicks = (int)(deadTime / Constants.ROBORIO_STEP_TIME_SEC);
		driveMemory = new LinkedList<Double>(Collections.nCopies(numDeadTimeTicks, 0.0));
	}

	/**
	 * Step the motor forward by one time increment.
	 * 
	 * @param drive the input signal during this time increment, |drive| <= 1.0
	 */
	public void step(double drive) {
	    // Add this drive value to the memory, then extract the oldest one to use for this step.
	    driveMemory.offerFirst(drive);
	    drive = driveMemory.pollLast();

		// Cap the drive value to +- 1.0.
		drive = Math.min(drive,  1.0);
		drive = Math.max(drive, -1.0);

		// Update motor state.
		currentAcceleration = (gain * drive - currentSpeed) / timeConstant;
		currentSpeed += Constants.ROBORIO_STEP_TIME_SEC * currentAcceleration;
		currentPosition += Constants.ROBORIO_STEP_TIME_SEC * currentSpeed;
	}
	
    /**
     * Get the current speed of the motor in ticks/second.
     */
    public double getAcceleration() {
        return currentAcceleration;
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

	/**
	 * Reset the motor back to idle state
	 */
	public void reset() {
		currentPosition = 0.0;
		currentSpeed = 0.0;
		currentAcceleration = 0.0;
		driveMemory.clear();
	}

	@Override
	public String toString() {
	    return "MotorModel[Kp=" + gain + ", Tp=" + timeConstant + ", Op=" + deadTime + "]";
	}
}
