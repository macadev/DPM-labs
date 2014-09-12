import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.*;
import lejos.nxt.comm.RConsole;

/**
 * Bang-Bang controller implementation
 *
 * Closed-loop controller the the wall follower robot.
 */
public class BangBangController implements UltrasonicController{
	private final int bandCenter, bandwith;

    private final int motorHigh, motorStraight;

    private static final int TURNLEFT = 1;
	private static final int TURNRIGHT = 0;
	
	
	private final NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.C;
	private int distance;
	private int previousDistance = 20;

    /**
     * Default constructor
     *
     * Initialises the member variables and starts the motor at a desired speed going forward
     * @param bandCenter desired distance from the wall
     * @param bandwith allowed region around the bandCenter
     * @param motorHigh fastest speed for the motors
     * @param motorStraight normal cruising speed
     */
	public BangBangController(int bandCenter, int bandwith, int motorHigh, int motorStraight) {
		//Default Constructor
		this.bandCenter = bandCenter;
		this.bandwith = bandwith;
		this.motorHigh = motorHigh;
        this.motorStraight = motorStraight;
		leftMotor.setSpeed(motorStraight);
		rightMotor.setSpeed(motorStraight);
		leftMotor.forward();
		rightMotor.forward();
	}

    /**
     * Bang-bang implementation
     *
     * Speeds up outside wheel if distance is too great
     * Speeds up inside wheel if distance is too little
     *
     * @param distance distance away from the wall
     */
	@Override
	public void processUSData(int distance) {
		this.distance = distance;
        int distanceThreshold = 20;
		//save previous distance - used to detect corners
		this.previousDistance = distance;
        if (this.distance < (this.bandCenter - this.bandwith)){
			//Too close to the wall, slow down outside wheel
			leftFaster();
		} else if (this.distance > (this.bandCenter + this.bandwith)) {
			//Too far from the wall, slow down inside wheel
			rightFaster();
        } else {
			// Robot is within bandwith
			bothStraight();
        }
        RConsole.println("Distance: " + String.valueOf(this.distance) + '\n' + "Speed: L->" + String.valueOf(leftMotor.getSpeed()) +
                " R->" + String.valueOf(rightMotor.getSpeed()));
	}

    /**
     * sets both motors to the same speed
     */
	public void bothStraight() {
		rightMotor.setSpeed(this.motorStraight);
		leftMotor.setSpeed(this.motorStraight);
	}

    /**
     * sets right motor to high speed, left to normal
     */
	public void rightFaster() {
		leftMotor.setSpeed(0);
		rightMotor.setSpeed(this.motorHigh);
	}

    /**
     * sets left motor to high speed, right to normal
     */
	public void leftFaster() {
		rightMotor.setSpeed(0);
		leftMotor.setSpeed(this.motorHigh);
	}

	@Override
	public int readUSDistance() {
		return this.distance;
	}
}
