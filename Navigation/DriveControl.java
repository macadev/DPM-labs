import lejos.nxt.NXTRegulatedMotor;

/**
 * Created by David on 14-09-25.
 * id. 260583602
 */
public class DriveControl extends Thread {
    private Odometer odometer;
    private NXTRegulatedMotor leftMotor, rightMotor;
    private double wheelRadius, width;

    /**
     * default constructor
     * @param odometer
     * @param leftMotor
     * @param rightMotor
     * @param width
     * @param wheelRadius
     */
    public DriveControl(Odometer odometer, NXTRegulatedMotor leftMotor, NXTRegulatedMotor rightMotor, double width, double wheelRadius){
        this.odometer=odometer;
        this.leftMotor = leftMotor;
        this.rightMotor = rightMotor;
        this.wheelRadius = wheelRadius;
        this.width = width;
    }

    public void travelTo(double x, double y){

    }

    /**
     * Rotates the robot to the desired angle using the optimal angle and direction
     * @param theta the desired angle to rotate to
     */
    public void turnTo(double theta){

        //implementation of slide 13 in navigation tutorial
        double thetaCurrent = this.odometer.getTheta();

        double rotationAngle = computeOptimalRotationAngle(thetaCurrent,theta);

        leftMotor.rotate(-ConversionUtilities.convertAngleToMotorRotation(wheelRadius, width, rotationAngle));
        rightMotor.rotate(ConversionUtilities.convertAngleToMotorRotation(wheelRadius, width, rotationAngle));

    }

    public boolean isNavigating(){

        return false;
    }

    /**
     * Compute the optimal way to get from angle a to angle b
     * @param currentTheta current heading
     * @param desiredTheta desired heading
     * @return the signed number of degrees to rotate, sign indicated direction
     */
    public double computeOptimalRotationAngle(double currentTheta, double desiredTheta){
        //implementation of slide 13 in navigation tutorial
        if (desiredTheta-currentTheta < -180){
            return (desiredTheta-currentTheta)+360;
        } else if (desiredTheta - currentTheta > 180){
            return desiredTheta - currentTheta - 360;
        } else {
            return desiredTheta - currentTheta;
        }
    }
}
