import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.*;
import lejos.nxt.comm.RConsole;

public class BangBangController implements UltrasonicController{
	private final int bandCenter, bandwith;

    private final int motorLow, motorHigh, motorStraight;

    private static final int TURNLEFT = 1;
	private static final int TURNRIGHT = 0;
	
	
	private final NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.C;
	private int distance;
	private int previousDistance = 20;

	public BangBangController(int bandCenter, int bandwith, int motorLow, int motorHigh, int motorStraight) {
		//Default Constructor
		this.bandCenter = bandCenter;
		this.bandwith = bandwith;
		this.motorLow = motorLow;
		this.motorHigh = motorHigh;
        this.motorStraight = motorStraight;
		leftMotor.setSpeed(motorStraight);
		rightMotor.setSpeed(motorStraight);
		leftMotor.forward();
		rightMotor.forward();
	}
	
	@Override
	public void processUSData(int distance) {
		this.distance = distance;
        int distanceThreshold = 20;
		// TODO: process a movement based on the us distance passed in (BANG-BANG style)
		
		//if previous measurement is much smaller than the current one, run corner protocol
		/*
		if (Math.abs(distance - this.previousDistance) > distanceThreshold) {
			RConsole.println("gap");
			openCornerTurn();
			this.distance = this.previousDistance;
		}
		*/
		
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
        RConsole.println("Distance: "+String.valueOf(this.distance)+'\n'+"Speed: L->"+String.valueOf(leftMotor.getSpeed())+
                " R->"+String.valueOf(rightMotor.getSpeed()));
	}

	
	public void openCornerTurn() {
		bothStraight();
		leftMotor.rotate(800, true);
		rightMotor.rotate(800);
		ninetyDegreeTurn(TURNLEFT);
		leftMotor.rotate(800, true);
		rightMotor.rotate(800);
	}
	
	public void ninetyDegreeTurn(int turnDirection) {
		if (turnDirection == 0){
			leftMotor.rotate(320, true);
			rightMotor.rotate(-320);
		} else {
			leftMotor.rotate(-320, true);
			rightMotor.rotate(320);
		}
	}
	
	public void bothStraight() {
		rightMotor.setSpeed(this.motorStraight);
		leftMotor.setSpeed(this.motorStraight);
	}
	
	public void rightFaster() {
		leftMotor.setSpeed(this.motorStraight);
		rightMotor.setSpeed(this.motorHigh);
	}
	
	public void leftFaster() {
		rightMotor.setSpeed(this.motorStraight);
		leftMotor.setSpeed(this.motorHigh);
	}

	@Override
	public int readUSDistance() {
		return this.distance;
	}
}
