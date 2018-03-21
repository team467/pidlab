package org.petrangelo.pidlab;

/**
 * PIDController implements a PID control function.
 */
public class PIDController {
	private final double kp;
	private final double ki;
	private final double kd;
	
	private double error = 0.0;

	/**
	 * Create a new PIDController
	 * 
	 * @param kp proportional term constant
	 * @param ki integral term constant
	 * @param kd derivative term constant
	 */
	public PIDController(double kp, double ki, double kd) {
		this.kp = kp;
		this.ki = ki;
		this.kd = kd;
	}
	
	/**
	 *  Set the error value in ticks. This is most useful for setting the initial error value.
	 */
	
	public void setError(double error) {
		this.error = error;
	}
	
	double calculate(double currentPosition, double currentSpeed, double stepTime, double targetPosition) {
		final double newError = targetPosition - currentPosition;
		
		
		final double pTerm = kp * error;
		final double iTerm = ki * 0.0;
		final double dTerm = kd * (newError - error) / stepTime;

		error = newError;
		
		return pTerm + iTerm + dTerm;
	}
}
