package org.shrewsburyrobotics.pidlab.model;

public class Constants {
	/**
	 * Time per iteration in seconds.
	 */
	public final static double STEP_TIME_SEC = 0.020;
	
	/**
	 * The drag on the motor caused by friction, the drive value is reduced by this amount,
	 * but never goes less than zero.
	 */
	public static final double MOTOR_FRICTION = 0.1;
}
