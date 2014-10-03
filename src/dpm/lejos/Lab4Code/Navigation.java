package dpm.lejos.Lab4Code;


import dpm.lejos.Lab3Code.ConversionUtilities;
import dpm.lejos.Lab3Code.Vector;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.comm.RConsole;

public class Navigation {
    private final int TURN_SPEED = 150;
    private final int STRAIGHT_SPEED = 200;

    private final static double ACCEPTABLE_ANGLE = 1.00;
    private final static double AGGEPTABLE_LINEAR = 1.00;

    private final int OBSTABLE_DISTANCE = 15;

    private double wheelRadius, width;

    // put your navigation code here

    private UltrasonicSensor us;

	private Odometer odometer;
	private TwoWheeledRobot robot;
	
	public Navigation(Odometer odo) {
		this.odometer = odo;
		this.robot = odo.getTwoWheeledRobot();
        this.wheelRadius = robot.getWheelRadius();
        this.width = robot.getWidth();
        this.us = robot.us;
	}
	
	/**
     * Given a point (x,y), this method will move the robot to those coordinates
     * @param x x coordinate
     * @param y y coordinate
     */
    public void travelTo(double x, double y){
        try {

            double[] currentPosition = new double[3];
            odometer.getPosition(currentPosition);

            Vector vector = vectorDisplacement(currentPosition, new double[]{x, y});

            RConsole.println("Magnitude: " + String.valueOf(vector.getMagnitude()));
            RConsole.println("Orientation: " + String.valueOf(vector.getOrientation()));
            turnTo(vector.getOrientation());

            robot.setForwardSpeed(STRAIGHT_SPEED);

            
            robot.leftMotor.rotate(ConversionUtilities.convertDistanceToMotorRotation(wheelRadius, vector.getMagnitude()), true);
            robot.rightMotor.rotate(ConversionUtilities.convertDistanceToMotorRotation(wheelRadius, vector.getMagnitude()), true);

            while(robot.isNavigating()){
                avoidObstacleDetection(OBSTABLE_DISTANCE);
            }


            if (!closeEnough(x, y)) {
                RConsole.println("Not close enough, redo!");
                travelTo(x, y);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Rotates the robot to the desired angle using the optimal angle and direction
     * @param theta the desired angle to rotate to
     */
    public void turnTo(double theta){



        //implementation of slide 13 in navigation tutorial
        double thetaCurrent = odometer.getTheta();

        double rotationAngle = computeOptimalRotationAngle(thetaCurrent,theta);

        robot.setRotationSpeed(TURN_SPEED);
        int angle = ConversionUtilities.convertAngleToMotorRotation(wheelRadius, width, rotationAngle);

        robot.leftMotor.rotate(-angle, true);
        robot.rightMotor.rotate(angle, false);


        if (!closeEnough(theta)){
            RConsole.println("Not close enough, redo!");
            turnTo(theta);
        }
    }

    /**
     * Travels forward a given distance
     *TODO: Change comment
     * @param distance     distance in centimeters
     */
    public void goForward(double distance) {
        robot.setForwardSpeed(STRAIGHT_SPEED);
        robot.leftMotor.rotate(ConversionUtilities.convertDistanceToMotorRotation(wheelRadius, distance), true);
        robot.rightMotor.rotate(ConversionUtilities.convertDistanceToMotorRotation(wheelRadius, distance), false);
    }

    /**
     * Travels backward a given distance
     *TODO: Change comment
     * @param distance     distance in centimeters
     */
    public void goBackward(double distance) {
        robot.setForwardSpeed(STRAIGHT_SPEED);
        robot.leftMotor.rotate(-ConversionUtilities.convertDistanceToMotorRotation(wheelRadius, distance), true);
        robot.rightMotor.rotate(-ConversionUtilities.convertDistanceToMotorRotation(wheelRadius, distance), false);
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

            if (x>=0) {
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

    /**
     * check if the robot faces an obstacle
     * @param distance distance at which we consider there is an obstacle
     */
    public void avoidObstacleDetection(int distance) {

        if (us.getDistance() < distance) {
            goAround();
        }
    }

    /**
     * drive in a square around the objet
     *
     * TODO: Implement better obstable avoidance tactic
     */
    public void goAround() {
        robot.leftMotor.stop();
        robot.rightMotor.stop();
        //Rotations:
        // turn 90 degrees clockwise
        robot.leftMotor.setSpeed(TURN_SPEED);
        robot.rightMotor.setSpeed(TURN_SPEED);
        robot.leftMotor.rotate(ConversionUtilities.convertAngleToMotorRotation(wheelRadius, width, Math.PI/2), true);
        robot.rightMotor.rotate(-ConversionUtilities.convertAngleToMotorRotation(wheelRadius, width, Math.PI/2), false);
        // drive forward
        robot.leftMotor.setSpeed(STRAIGHT_SPEED);
        robot.rightMotor.setSpeed(STRAIGHT_SPEED);
        robot.leftMotor.rotate(ConversionUtilities.convertDistanceToMotorRotation(wheelRadius, 25), true);
        robot.rightMotor.rotate(ConversionUtilities.convertDistanceToMotorRotation(wheelRadius, 25), false);
        // turn 90 degrees counterclockwise
        robot.leftMotor.setSpeed(TURN_SPEED);
        robot.rightMotor.setSpeed(TURN_SPEED);
        robot.leftMotor.rotate(-ConversionUtilities.convertAngleToMotorRotation(wheelRadius, width, Math.PI/2), true);
        robot.rightMotor.rotate(ConversionUtilities.convertAngleToMotorRotation(wheelRadius, width, Math.PI/2), false);
        // drive forward
        robot.leftMotor.setSpeed(STRAIGHT_SPEED);
        robot.rightMotor.setSpeed(STRAIGHT_SPEED);
        robot.leftMotor.rotate(ConversionUtilities.convertDistanceToMotorRotation(wheelRadius, 45), true);
        robot.rightMotor.rotate(ConversionUtilities.convertDistanceToMotorRotation(wheelRadius, 45), false);
        // turn 90 degrees counterclockwise
        robot.leftMotor.setSpeed(TURN_SPEED);
        robot.rightMotor.setSpeed(TURN_SPEED);
        robot.leftMotor.rotate(-ConversionUtilities.convertAngleToMotorRotation(wheelRadius, width, Math.PI/2), true);
        robot.rightMotor.rotate(ConversionUtilities.convertAngleToMotorRotation(wheelRadius, width, Math.PI/2), false);
        // drive forward
        robot.leftMotor.setSpeed(STRAIGHT_SPEED);
        robot.rightMotor.setSpeed(STRAIGHT_SPEED);
        robot.leftMotor.rotate(ConversionUtilities.convertDistanceToMotorRotation(wheelRadius, 25), true);
        robot.rightMotor.rotate(ConversionUtilities.convertDistanceToMotorRotation(wheelRadius, 25), false);
        // turn 90 degrees clockwise
        robot.leftMotor.setSpeed(TURN_SPEED);
        robot.rightMotor.setSpeed(TURN_SPEED);
        robot.leftMotor.rotate(ConversionUtilities.convertAngleToMotorRotation(wheelRadius, width, Math.PI/2), true);
        robot.rightMotor.rotate(-ConversionUtilities.convertAngleToMotorRotation(wheelRadius, width, Math.PI/2), false);
        robot.leftMotor.stop();
        robot.rightMotor.stop();
    }
}
