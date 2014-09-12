import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.*;
import lejos.nxt.comm.RConsole;

public class PController implements UltrasonicController {
	
	private final int bandCenter, bandwith;
	private final int motorStraight, motorHigh, FILTER_OUT = 20;
	private final NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.C;	
	private int distance;
	private int filterControl;
    private static int PROPORTIONAL_CONSTANT = 2;
	
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
			leftMotor.setSpeed(Math.min(ratio*motorStraight*PROPORTIONAL_CONSTANT, this.motorHigh)+motorStraight);
			rightMotor.setSpeed(motorStraight);
		} else if (this.distance > (this.bandCenter+this.bandwith)) {
			//Too far from the wall, speed up outside wheel
			int error = this.distance - this.bandCenter;
			float ratio = error/(float)this.bandCenter;
			rightMotor.setSpeed(Math.min(ratio*motorStraight*PROPORTIONAL_CONSTANT, this.motorHigh)+motorStraight);
			leftMotor.setSpeed(motorStraight);
				
		} else {
			// we assume that the value is in the center band as it is not above or below
			rightMotor.setSpeed(motorStraight);
			leftMotor.setSpeed(motorStraight);
		}
        RConsole.println("Distance: " + String.valueOf(this.distance) + '\n' + "Speed: L->" + String.valueOf(leftMotor.getSpeed()) +
                " R->" + String.valueOf(rightMotor.getSpeed()));

    }

	
	@Override
	public int readUSDistance() {
		return this.distance;
	}

}
