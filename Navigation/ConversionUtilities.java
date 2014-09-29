/**
 * Created by David on 14-09-26.
 * id. 260583602
 */
public class ConversionUtilities {
    /**
     * Conversion from desired travel distance to motor rotation angle (tacho count)
     * @param wheelRadius radius of the wheels
     * @param distance distance to travel
     * @return number of degrees the motor should travel to match the desired distance
     */
    public static int convertDistanceToMotorRotation(double wheelRadius, double distance) {
        return (int) ((180.0 * distance) / (Math.PI * wheelRadius));
    }

    /**
     * Conversion from desired rotation of the robot to motor angle (tacho count)
     *
     * This assumes that the robot uses skid rotation and that the two motors will travel in opposite directions
     *
     * @param wheelRadius the radius of the wheel
     * @param width width of the wheel base, or distance between the wheels
     * @param angle the angle the robot should rotate in radians
     * @return the angle a motor should travel for the robot to rotate
     */
    public static int convertAngleToMotorRotation(double wheelRadius, double width, double angle) {
        return convertDistanceToMotorRotation(wheelRadius, 2*Math.PI * width * angle / 180.0);
    }
}
