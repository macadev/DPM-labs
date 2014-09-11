import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.*;

public class PController implements UltrasonicController {
	
	private final int bandCenter, bandwith;
	private final int motorStraight = 200, FILTER_OUT = 20;
	private final NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.C;	
	private int distance;
	private int currentLeftSpeed;
	private int filterControl;
	
	public PController(int bandCenter, int bandwith) {
		//Default Constructor
		this.bandCenter = bandCenter;
		this.bandwith = bandwith;
		leftMotor.setSpeed(motorStraight);
		rightMotor.setSpeed(motorStraight);
		leftMotor.forward();
		rightMotor.forward();
		currentLeftSpeed = 0;
		filterControl = 0;
	}
	
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
		// TODO: process a movement based on the us distance passed in (P style)
		if (this.distance < (this.bandCenter-this.bandwith)){
			//Too close to the wall, speed up inside wheel
			int error = this.bandCenter - this.distance;
			float ratio = error/(float)this.bandCenter;
			leftMotor.setSpeed(ratio*motorStraight+motorStraight);
			rightMotor.setSpeed(motorStraight);
		} else if (this.distance > (this.bandCenter+this.bandwith)) {
			//Too far from the wall, speed up outside wheel
			int error = this.distance - this.bandCenter;
			float ratio = error/(float)this.bandCenter;
			rightMotor.setSpeed(ratio*motorStraight+motorStraight);
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
