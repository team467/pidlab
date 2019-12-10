package org.shrewsburyrobotics.pidlab.model;

/**
 * RobotModel simulates the 2018 robot as its input signal is changed over time.
 * 
 * The RobotModel is based on a simpler MotorModel, but with units converted
 * to match the units useful on a robot (i.e. feet instead of ticks, etc.).
 */
public class Robot2020Model extends MotorModel {
    //TODO: change these constants
    static final double INCHES_PER_REVOLUTION = 18.50;
    static final double FEET_PER_REVOLUTION = INCHES_PER_REVOLUTION / 12;
    static final double TICKS_PER_REVOLUTION = 1024;
    static final double TICKS_PER_FOOT = TICKS_PER_REVOLUTION / FEET_PER_REVOLUTION;

	/**
	 * Create a MotorModel object describing the 2018 robot.
	 * 
	 * @param gain motor gain (Kp), or the maximum speed of the motor at full power,
	 *        in feet/second
     * @param timeConstant time constant (Tp) for the motor, or the time it takes for
     *        the motor to reach ~62% of the final speed at a given input, in seconds
     * @param deadTime dead time (theta P) is the time between a change in input and
     *        when a measurable response occurs, in seconds
	 */
    public Robot2020Model(double gain, double timeConstant, double deadTime) {
        super(feetToTicks(gain), timeConstant, deadTime);
    }

    private static double ticksToFeet(double ticks) {
        return ticks / TICKS_PER_FOOT;
    }
    
    private static double feetToTicks(double feet) {
        return feet  * TICKS_PER_FOOT;
    }
    
	/**
	 * Get the current speed of the motor in ticks/second.
	 */
	public double getSpeed() {
		return ticksToFeet(super.getSpeed());
	}

	/**
	 * Get the current position of the motor in ticks.
	 */
	public double getPosition() {
		return ticksToFeet(super.getPosition());
	}

	@Override
    public String toString() {
        return String.format("RobotModel [speed=%s in/s, position=%s in]",
                getSpeed(), getPosition());
    }
}
