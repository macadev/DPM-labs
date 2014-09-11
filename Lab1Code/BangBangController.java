import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.*;
import lejos.nxt.comm.RConsole;

public class BangBangController implements UltrasonicController{
	private final int bandCenter, bandwith;
	private final int motorLow = 50, motorHigh=200;
	private final int motorStraight = 150;
	private final NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.C;
	private int distance;
	private int currentLeftSpeed;
	
	public BangBangController(int bandCenter, int bandwith, int motorLow, int motorHigh) {
		//Default Constructor
		this.bandCenter = bandCenter;
		this.bandwith = bandwith;
		//this.motorLow = motorLow;
		//this.motorHigh = motorHigh;
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
			rightMotor.setSpeed(motorStraight);
			leftMotor.setSpeed(motorHigh);

		} else if (this.distance > (this.bandCenter+this.bandwith)) {
			//Too far from the wall, slow down inside wheel
			leftMotor.setSpeed(motorStraight);
			rightMotor.setSpeed(motorHigh);
        } else {
			// we assume that the value is in the center band as it is not above or below
			rightMotor.setSpeed(motorStraight);
			leftMotor.setSpeed(motorStraight);
        }
        RConsole.println("Distance: "+String.valueOf(this.distance)+'\n'+"Speed: L->"+String.valueOf(leftMotor.getSpeed())+
                " R->"+String.valueOf(rightMotor.getSpeed()));
	}

	@Override
	public int readUSDistance() {
		return this.distance;
	}
}
