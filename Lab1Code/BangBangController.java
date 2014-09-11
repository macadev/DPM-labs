import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.*;

public class BangBangController implements UltrasonicController{
	private final int bandCenter, bandwith;
	private final int motorLow, motorHigh;
	private final int motorStraight = 200;
	private final NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.C;
	private int distance;
	private int currentLeftSpeed;
	
	public BangBangController(int bandCenter, int bandwith, int motorLow, int motorHigh) {
		//Default Constructor
		this.bandCenter = bandCenter;
		this.bandwith = bandwith;
		this.motorLow = motorLow;
		this.motorHigh = motorHigh;
		leftMotor.setSpeed(motorStraight);
		rightMotor.setSpeed(motorStraight);
		leftMotor.forward();
		rightMotor.forward();
		currentLeftSpeed = 0;
	}
	
	@Override
	public void processUSData(int distance) {
		this.distance = distance;
		// TODO: process a movement based on the us distance passed in (BANG-BANG style)
		if (this.distance < (this.bandCenter-this.bandwith)){
			//Too close to the wall, slow down outside wheel
			leftMotor.setSpeed(motorLow);
			rightMotor.setSpeed(motorStraight);
		} else if (this.distance > (this.bandCenter+this.bandwith)) {
			//Too far from the wall, slow down inside wheel
			rightMotor.setSpeed(motorLow);
			leftMotor.setSpeed(motorStraight);
		} else {
			// we assume that the value is in the center band as it is not above or below
			rightMotor.setSpeed(motorStraight);
			leftMotor.setSpeed(motorStraight);
		}
		
	}

	@Override
	public int readUSDistance() {
		return this.distance;
	}
}
