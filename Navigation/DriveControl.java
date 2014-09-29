import lejos.nxt.NXTRegulatedMotor;

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

    private boolean navigating = false;
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

    public void run(){
        travelTo(60,30);
        travelTo(30,30);
        travelTo(30,60);
        travelTo(60, 0);
    }

    public void travelTo(double x, double y){

        navigating = true;
        double [] currentPosition = new double[3];
        odometer.getPosition(currentPosition, new boolean[]{true, true, true});

        Vector vector = vectorDisplacement(currentPosition, new double[]{x, y});

        turnTo(vector.getOrientation());

        leftMotor.setSpeed(STRAIGHT_SPEED);
        rightMotor.setSpeed(STRAIGHT_SPEED);

        leftMotor.rotate(ConversionUtilities.convertDistanceToMotorRotation(wheelRadius, vector.getMagnitude()), true);
        rightMotor.rotate(ConversionUtilities.convertDistanceToMotorRotation(wheelRadius, vector.getMagnitude()), false);
        navigating = false;
        if (!closeEnough(x, y)){
            travelTo(x,y);
        }
    }

    /**
     * Rotates the robot to the desired angle using the optimal angle and direction
     * @param theta the desired angle to rotate to
     */
    public void turnTo(double theta){

        navigating =true;

        //implementation of slide 13 in navigation tutorial
        double thetaCurrent = this.odometer.getTheta();

        double rotationAngle = computeOptimalRotationAngle(thetaCurrent,theta);

        leftMotor.setSpeed(TURN_SPEED);
        rightMotor.setSpeed(TURN_SPEED);
        leftMotor.rotate(-ConversionUtilities.convertAngleToMotorRotation(wheelRadius, width, rotationAngle), true);
        rightMotor.rotate(ConversionUtilities.convertAngleToMotorRotation(wheelRadius, width, rotationAngle), false);

        navigating = false;
        if (!closeEnough(theta)){
            turnTo(theta);
        }
    }

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
        if (desiredTheta-currentTheta < -180){
            return (desiredTheta-currentTheta)+360;
        } else if (desiredTheta - currentTheta > 180){
            return desiredTheta - currentTheta - 360;
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

    public boolean closeEnough(double x, double y) {
        return Math.abs(x - odometer.getX()) < 1.00 && Math.abs(y - odometer.getY()) < 1.00;
    }

    public boolean closeEnough(double theta) {
        return Math.abs(theta - odometer.getTheta()) <= Math.toDegrees(1.00);
    }

}
