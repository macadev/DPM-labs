import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.*;
import lejos.nxt.comm.RConsole;
import lejos.util.NXTDataLogger;

public class BangBangController implements UltrasonicController{
	private final int bandCenter, bandwith;
	private final int motorLow = 50, motorHigh = 400;
	private final int motorStraight = 200;
	private final int TURNLEFT = 1;
	private final int TURNRIGHT = 0;
	
	
	private final NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.C;
	private int distance;
	private int previousDistance = 20;
	private int currentLeftSpeed;
    private NXTDataLogger dlog;
	
	public BangBangController(int bandCenter, int bandwith, int motorLow, int motorHigh, NXTDataLogger dlog) {
		//Default Constructor
		this.bandCenter = bandCenter;
		this.bandwith = bandwith;
        this.dlog = dlog;
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
		
		//if previous measurement is much smaller than the current one, run corner protocol
		/*
		if (Math.abs(distance - this.previousDistance) > 20) {
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
        dlog.writeLog(leftMotor.getSpeed());
        dlog.writeLog(rightMotor.getSpeed());
        dlog.writeLog(this.distance);
	}

	
	public void openCornerTurn() {
		bothStraight();
		leftMotor.rotate(800, true);
		rightMotor.rotate(800);
		ninetyDegreeTurn(this.TURNLEFT);
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
		rightMotor.setSpeed(motorStraight);
		leftMotor.setSpeed(motorStraight);
	}
	
	public void rightFaster() {
		leftMotor.setSpeed(motorStraight);
		rightMotor.setSpeed(motorHigh);
	}
	
	public void leftFaster() {
		rightMotor.setSpeed(motorStraight);
		leftMotor.setSpeed(motorHigh);
	}

	@Override
	public int readUSDistance() {
		return this.distance;
	}
}
