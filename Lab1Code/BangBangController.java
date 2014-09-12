import lejos.nxt.*;
import lejos.nxt.comm.RConsole;

/**
 * Bang-Bang controller implementation
 *
 * Closed-loop controller the the wall follower robot.
 */
public class BangBangController implements UltrasonicController{
	private final int bandCenter, bandwith;

    private final int FILTER_OUT = 10;
    private int filterControl;

    private final int motorHigh, motorStraight;
	
	private final NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.C;
	private int distance;

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
        filterControl = 0;
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
        // rudimentary filter
        if (distance == 255 && filterControl < FILTER_OUT) {
            // bad value, do not set the distance var, however do increment the filter value
            filterControl ++;
        } else if (distance == 255){
            // true 255, therefore set distance to 255
            this.distance = distance;
        } else {
            // distance went below 255, therefore reset everything.
            filterControl = 0;
            this.distance = distance;
        }

		//save previous distance - used to detect corners
        if (this.distance < (this.bandCenter - this.bandwith)){
			//Too close to the wall, slow down outside wheel
			leftFaster();
		} else if (this.distance > (this.bandCenter + this.bandwith)) {
			//Too far from the wall, slow down inside wheel
			rightFaster();
        } else {
			// Robot is within bandwith
			bothMotorsStraight();
        }
        
        printMotorDistances();
	}

    /**
     * sets both motors to the same speed
     */
	public void bothMotorsStraight() {
		rightMotor.setSpeed(this.motorStraight);
		leftMotor.setSpeed(this.motorStraight);
	}

    /**
     * sets right motor to high speed, left to normal
     */
	public void rightFaster() {
		leftMotor.setSpeed(100);
		rightMotor.setSpeed(this.motorHigh);
	}

    /**
     * sets left motor to high speed, right to normal
     */
	public void leftFaster() {
		rightMotor.setSpeed(100);
		leftMotor.setSpeed(this.motorHigh);
	}
	
	/**
     * prints to usb console the current speeds of both motors
     */
	public void printMotorDistances() {
		RConsole.println("Distance: " + String.valueOf(this.distance) +
				'\n' + "Speed: L->" + String.valueOf(leftMotor.getSpeed()) +
                " R->" + String.valueOf(rightMotor.getSpeed()));
	}

	@Override
	public int readUSDistance() {
		return this.distance;
	}
}
