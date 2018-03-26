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
		int numDeadTimeTicks = (int)(deadTime / Constants.STEP_TIME_SEC);
		driveMemory = new LinkedList<Double>(Collections.nCopies(numDeadTimeTicks, 0.0));
	}

	/**
	 * Adjust for friction be reducing the input value by 0.1 (closer to zero).
	 * 
	 * TODO John: I don't think this is modeled correctly yet.
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
	    // Add this drive value to the memory, then extract the oldest one to use for this step.
	    driveMemory.offerFirst(drive);
	    drive = driveMemory.pollLast();
	    
		// Cap the drive value to +- 1.0.
		drive = Math.min(drive,  1.0);
		drive = Math.max(drive, -1.0);

		drive = adjustForFriction(drive);
		
		// Update motor state.
		currentSpeed += Constants.STEP_TIME_SEC * (gain * drive - currentSpeed) / timeConstant;
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

	/**
	 * Reset the motor back to idle state
	 */
	public void reset() {
		currentPosition = 0;
		currentSpeed = 0;
		driveMemory.clear();
	}

	@Override
	public String toString() {
	    return "MotorModel[Kp=" + gain + ", Tp=" + timeConstant + ", Op=" + deadTime + "]";
	}
}
