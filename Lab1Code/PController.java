import lejos.nxt.*;
import lejos.nxt.comm.RConsole;
import lejos.robotics.navigation.DifferentialPilot;

public class PController implements UltrasonicController {
	
	private final int bandCenter, bandwith;
	private final int motorStraight, FILTER_OUT = 20;
	private final NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.C;	
	private int distance;
	private int filterControl;
    private static float PROPORTIONAL_CONSTANT = (float) 2;

    /**
     * Default constructor
     *
     * Initialises the member variables and starts the motor at a desired speed going forward
     * @param bandCenter desired distance from the wall
     * @param bandwith allowed buffer region
     * @param motorStraight normal cruisong speed
     */
	public PController(int bandCenter, int bandwith, int motorStraight) {
		//Default Constructor
		this.bandCenter = bandCenter;
		this.bandwith = bandwith;
        this.motorStraight = motorStraight;

		leftMotor.setSpeed(motorStraight);
		rightMotor.setSpeed(motorStraight);
		leftMotor.forward();
		rightMotor.forward();
		filterControl = 0;
	}

    /**
     * Proportional controller implementation
     *
     * speeds up and down wheels proportionnaly to the distance to the wall
     *
     * @param distance distance to the wall
     */
	@Override
	public void processUSData(int distance) {
		
		// rudimentary filter
		if (distance > 255 && filterControl < FILTER_OUT) {
			// Record that a bad value was detected. Record it and keep executing
			filterControl ++;
		} else if (distance > 255){
			// true 255, therefore set distance to 255
			this.distance = distance;
		} else {
			// distance went below 255, therefore reset everything.
			filterControl = 0;
			this.distance = distance;
		}
		
		if (this.distance < (this.bandCenter - this.bandwith)) {
			//Too close to the wall, speed up inside wheel
            float differential = recalculateSpeed();
            leftMotorFaster(differential);
		} else if (this.distance > (this.bandCenter + this.bandwith)) {
			//Too far from the wall, speed up outside wheel
            float differential = recalculateSpeed();
            rightMotorFaster(differential);	
		} else {
			// we assume that the value is in the center band as it is not above or below
			bothMotorsStraight();
		}
        
		printMotorDistances();

    }
	
	/**
     * sets right motor to higher speed proportional to error,
     * sets left motor to normal speed to increase turning radius
     * allow for full 180 turns more easilly
     *
     */
	public void rightMotorFaster( float differential ) {
		this.rightMotor.setSpeed(differential + motorStraight);
        this.leftMotor.setSpeed(motorStraight);
	}
	
	/**
     * sets left motor to higher speed proportional to error,
     * sets right motor to lower speed proportional to error.
     */
	public void leftMotorFaster( float differential ) {
		this.leftMotor.setSpeed(differential + motorStraight);
        //make sure speed is at least 1
        this.rightMotor.setSpeed(Math.max(1 , motorStraight - differential));
	}
	
	/**
     * sets both motors to the same speed
     */
	public void bothMotorsStraight() {
		rightMotor.setSpeed(this.motorStraight);
		leftMotor.setSpeed(this.motorStraight);
	}
	
	/**
     * returns quantity to modify motor speeds. 
     * Calculation based on the current distance, band center,
     * and a proportionality constant.
     */
	public float recalculateSpeed() {
		int error = Math.abs(this.bandCenter - this.distance);
		float ratio = error / (float) this.bandCenter;
        return Math.min(ratio * motorStraight * PROPORTIONAL_CONSTANT, this.motorStraight);
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
