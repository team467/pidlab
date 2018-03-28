package org.shrewsburyrobotics.pidlab.model;

/**
 * PIDController implements a PID control function.
 */
public class PIDController {
	private final double kp;
	private final double ki;
	private final double kd;
	
	private double error = 0.0;
	private double accumError = 0.0;

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
		accumError = error;
	}
	
	public double calculate(double currentPosition, double currentSpeed, double stepTime, double targetPosition) {
		final double newError = targetPosition - currentPosition;
		accumError += newError;

		// Note: The Talon SRX Speed Controller updates its control loop every 1 ms. Further, we have
		//       observed in the Talon's PID control loop source code that they don't take into account
		//       the cycle time when calculating the I and D terms. To match their implementation we also
		//       will not take into account the cycle time. Further, the change in error calculated for
		//       use by the D term is 20 times bigger for us than for the Talon because we are using a
		//       cycle time that is 20 times bigger than that used by the Talon. Therefore we divide our
		//       D term by 20 to use the same scale for the D constant.
		final double pTerm = kp * newError;
        final double iTerm = ki * accumError;
        final double dTerm = kd * (newError - error) / 20;

        // Ideally, the I and D terms should look like the following:
        //     final double iTerm = ki * accumError * stepTime;
        //     final double dTerm = kd * (newError - error) / stepTime;

		error = newError;
		
		return pTerm + iTerm + dTerm;
	}
}
