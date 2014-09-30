import lejos.nxt.*;
import lejos.nxt.comm.RConsole;

/**
 * Created by David on 14-09-25.
 * id. 260583602
 */
public class DriveControl extends Thread {
    private Odometer odometer;
    private NXTRegulatedMotor leftMotor, rightMotor;
    private double wheelRadius, width;

    private final int TURN_SPEED = 150;
    private final int STRAIGHT_SPEED = 200;

    private final static double ACCEPTABLE_ANGLE = 1.00;
    private final static double AGGEPTABLE_LINEAR = 1.00;

    private double currentXtarget;
    private double currentYtarget;

    Exception obstacleException = new Exception("Obstable too close");

    private boolean navigating = false;
    /**
     * default constructor
     * @param odometer Odometer object
     * @param leftMotor reference to the left motor object
     * @param rightMotor reference to the right motor
     * @param width with of the wheel space
     * @param wheelRadius radius of each wheel
     */
    public DriveControl(Odometer odometer, NXTRegulatedMotor leftMotor, NXTRegulatedMotor rightMotor, double width, double wheelRadius){
        this.odometer=odometer;
        this.leftMotor = leftMotor;
        this.rightMotor = rightMotor;
        this.wheelRadius = wheelRadius;
        this.width = width;
    }



    /**
     * Given a point (x,y), this method will move the robot to those coordinates
     * @param x x coordinate
     * @param y y coordinate
     */
    public void travelTo(double x, double y){
        currentXtarget = x;
        currentYtarget = y;
        try {

            navigating = true;
            double[] currentPosition = new double[3];
            odometer.getPosition(currentPosition, new boolean[]{true, true, true});

            Vector vector = vectorDisplacement(currentPosition, new double[]{x, y});

            turnTo(vector.getOrientation());

            leftMotor.setSpeed(STRAIGHT_SPEED);
            rightMotor.setSpeed(STRAIGHT_SPEED);

            leftMotor.rotate(ConversionUtilities.convertDistanceToMotorRotation(wheelRadius, vector.getMagnitude()), true);
            rightMotor.rotate(ConversionUtilities.convertDistanceToMotorRotation(wheelRadius, vector.getMagnitude()), false);
            navigating = false;
            if (!closeEnough(x, y)) {
                RConsole.println("Not close enough, redo!");
                travelTo(x, y);
            }
        }
        catch (Exception e){
          return;
        }
    }

    /**
     * Rotates the robot to the desired angle using the optimal angle and direction
     * @param theta the desired angle to rotate to
     */
    public void turnTo(double theta){

        navigating =true;

        //implementation of slide 13 in navigation tutorial
        double thetaCurrent = odometer.getTheta();

        double rotationAngle = computeOptimalRotationAngle(thetaCurrent,theta);

        leftMotor.setSpeed(TURN_SPEED);
        rightMotor.setSpeed(TURN_SPEED);
        int angle = ConversionUtilities.convertAngleToMotorRotation(wheelRadius, width, rotationAngle);

        leftMotor.rotate(-angle, true);
        rightMotor.rotate(angle, false);

        navigating = false;
        if (!closeEnough(theta)){
            RConsole.println("Not close enough, redo!");
            turnTo(theta);
        }
    }

    /**
     * Check if the robot is travelling
     * @return is the robot travelling
     */
    public boolean isNavigating(){

        return navigating;

    }

    /**
     * Compute the optimal way to get from angle a to angle b
     * @param currentTheta current heading
     * @param desiredTheta desired heading
     * @return the signed number of degrees to rotate, sign indicated direction
     */
    public static double computeOptimalRotationAngle(double currentTheta, double desiredTheta){
        //implementation of slide 13 in navigation tutorial
        if (desiredTheta-currentTheta < -Math.PI){
            return (desiredTheta-currentTheta)+2* Math.PI;
        } else if (desiredTheta - currentTheta > Math.PI){
            return desiredTheta - currentTheta - 2* Math.PI;
        } else {
            return desiredTheta - currentTheta;
        }
    }

    /**
     * Converts a set of coordinates in a vector displacement with orientation and magnitude
     * @param currentPosition array of 3 elements respectively (x, y, theta) representing the current psotition and orientation
     * @param destination array of 2 elements being (x, y)
     * @return Vector representing the displacement to happen (r, theta)
     */
    public static Vector vectorDisplacement(double[] currentPosition, double[] destination){
        Vector vector = new Vector();
        if (currentPosition.length == 3 && destination.length == 2){
            //expnaded pythagora
            vector.setMagnitude(Math.sqrt(destination[0] * destination[0] - 2 * destination[0] * currentPosition[0] + currentPosition[0] * currentPosition[0] + destination[1] * destination[1] - 2 * destination[1] * currentPosition[1] + currentPosition[1] * currentPosition[1]));

            double x = destination[0] - currentPosition[0];
            double y = destination[1] - currentPosition[1];

            if (x>0) {
                vector.setOrientation(Math.atan((y) / (x)));
            } else if (x<0 && y>0){
                vector.setOrientation(Math.atan((y) / (x)) + Math.PI);
            } else if (x<0 && y<0){
                vector.setOrientation(Math.atan((y) / (x))-Math.PI);
            }
        }
    return vector;
    }

    /**
     * Check if the coordinates of the robot are within an acceptable range to those specified
     * @param x target x coordinate
     * @param y target y coordinate
     * @return boolean true if in acceptable range
     */
    public boolean closeEnough(double x, double y) {
        return Math.abs(x - odometer.getX()) < AGGEPTABLE_LINEAR && Math.abs(y - odometer.getY()) < AGGEPTABLE_LINEAR;
    }

    /**
     * Check if the orientaition of the robot is within an acceptable range of the specified angle
     * @param theta target orientation
     * @return boolean true if in acceptable range
     * */
    public boolean closeEnough(double theta) {
        return Math.abs(theta - odometer.getTheta()) <= Math.toDegrees(ACCEPTABLE_ANGLE);
    }

    public void avoidObstacleDetection(int distance) throws Exception {
        if (distance < 15) {
            avoid();

            throw obstacleException;

        }
    }

    public void avoid() {
        leftMotor.stop();
        rightMotor.stop();
        //Rotations:
        // turn 90 degrees clockwise
        leftMotor.setSpeed(TURN_SPEED);
        rightMotor.setSpeed(TURN_SPEED);
        leftMotor.rotate(ConversionUtilities.convertAngleToMotorRotation(wheelRadius, width, 90), true);
        rightMotor.rotate(-ConversionUtilities.convertAngleToMotorRotation(wheelRadius, width, 90.0), false);
        // drive forward
        leftMotor.setSpeed(STRAIGHT_SPEED);
        rightMotor.setSpeed(STRAIGHT_SPEED);
        leftMotor.rotate(ConversionUtilities.convertDistanceToMotorRotation(wheelRadius, 25), true);
        rightMotor.rotate(ConversionUtilities.convertDistanceToMotorRotation(wheelRadius, 25), false);
        // turn 90 degrees counterclockwise
        leftMotor.setSpeed(TURN_SPEED);
        rightMotor.setSpeed(TURN_SPEED);
        leftMotor.rotate(-ConversionUtilities.convertAngleToMotorRotation(wheelRadius, width, 90), true);
        rightMotor.rotate(ConversionUtilities.convertAngleToMotorRotation(wheelRadius, width, 90.0), false);
        // drive forward
        leftMotor.setSpeed(STRAIGHT_SPEED);
        rightMotor.setSpeed(STRAIGHT_SPEED);
        leftMotor.rotate(ConversionUtilities.convertDistanceToMotorRotation(wheelRadius, 45), true);
        rightMotor.rotate(ConversionUtilities.convertDistanceToMotorRotation(wheelRadius, 45), false);
        // turn 90 degrees counterclockwise
        leftMotor.setSpeed(TURN_SPEED);
        rightMotor.setSpeed(TURN_SPEED);
        leftMotor.rotate(-ConversionUtilities.convertAngleToMotorRotation(wheelRadius, width, 90), true);
        rightMotor.rotate(ConversionUtilities.convertAngleToMotorRotation(wheelRadius, width, 90.0), false);
        // drive forward
        leftMotor.setSpeed(STRAIGHT_SPEED);
        rightMotor.setSpeed(STRAIGHT_SPEED);
        leftMotor.rotate(ConversionUtilities.convertDistanceToMotorRotation(wheelRadius, 25), true);
        rightMotor.rotate(ConversionUtilities.convertDistanceToMotorRotation(wheelRadius, 25), false);
        // turn 90 degrees clockwise
        leftMotor.setSpeed(TURN_SPEED);
        rightMotor.setSpeed(TURN_SPEED);
        leftMotor.rotate(ConversionUtilities.convertAngleToMotorRotation(wheelRadius, width, 90), true);
        rightMotor.rotate(-ConversionUtilities.convertAngleToMotorRotation(wheelRadius, width, 90.0), false);
        leftMotor.stop();
        rightMotor.stop();
    }

}
