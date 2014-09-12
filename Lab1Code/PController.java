import lejos.nxt.*;
import lejos.nxt.comm.RConsole;
import lejos.robotics.navigation.DifferentialPilot;

public class PController implements UltrasonicController {
	
	private final int bandCenter, bandwith;
	private final int motorStraight, motorHigh, FILTER_OUT = 10;
	private final NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.C;	
	private int distance;
	private int filterControl;
    private static float PROPORTIONAL_CONSTANT = (float) 2.5;
	
	public PController(int bandCenter, int bandwith,int motorStraight, int motorHigh) {
		//Default Constructor
		this.bandCenter = bandCenter;
		this.bandwith = bandwith;
        this.motorStraight = motorStraight;
        this.motorHigh = motorHigh;
        
		leftMotor.setSpeed(motorStraight);
		rightMotor.setSpeed(motorStraight);
		leftMotor.forward();
		rightMotor.forward();
		filterControl = 0;
	}
	
	@Override
	public void processUSData(int distance) {
		
		// rudimentary filter
		if (distance > 150 && filterControl < FILTER_OUT) {
			// Record that a bad value was detected. Record it and keep executing
			filterControl ++;
		} else if (distance > 150){
			// true 255, therefore set distance to 255
			this.distance = distance;
		} else {
			// distance went below 255, therefore reset everything.
			filterControl = 0;
			this.distance = distance;
		}
		
		// TODO: process a movement based on the us distance passed in (P style)
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
     * sets left motor to lower speed proportional to error.
     */
	public void rightMotorFaster( float differential ) {
		this.rightMotor.setSpeed(differential + motorStraight);
		
		if (this.leftMotor.getSpeed() - differential <= 0) {
			return;
		} else {
			this.leftMotor.setSpeed(motorStraight - differential);
		}
	}
	
	/**
     * sets left motor to higher speed proportional to error,
     * sets right motor to lower speed proportional to error.
     */
	public void leftMotorFaster( float differential ) {
		this.leftMotor.setSpeed(differential + motorStraight);
		
		if (this.rightMotor.getSpeed() - differential <= 0) {
			return;
		} else {
			this.rightMotor.setSpeed(motorStraight - differential);
		}
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
		float differential = Math.min( ratio * motorStraight * PROPORTIONAL_CONSTANT, this.motorHigh);
		return differential;
	}
	
	/**
     * prints to LCD the current speeds of both motors
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
