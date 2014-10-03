package dpm.lejos.Lab4Code;

import dpm.lejos.Lab3Code.ConversionUtilities;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.UltrasonicSensor;

public class TwoWheeledRobot extends ConversionUtilities{
	public static final double DEFAULT_WHEEL_RADIUS = 2.75;
	public static final double DEFAULT_WIDTH = 15.8;
	public NXTRegulatedMotor leftMotor, rightMotor;
	private double wheelRadius, width;
	private double forwardSpeed, rotationSpeed;
	public UltrasonicSensor us;

	public TwoWheeledRobot(NXTRegulatedMotor leftMotor,
						   NXTRegulatedMotor rightMotor,
						   double width,
						   double wheelRadius) {
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.wheelRadius = wheelRadius;
		this.width = width;
	}

    public TwoWheeledRobot(NXTRegulatedMotor leftMotor, NXTRegulatedMotor rightMotor) {
        this(leftMotor, rightMotor, DEFAULT_WIDTH, DEFAULT_WHEEL_RADIUS);
    }
    public TwoWheeledRobot(NXTRegulatedMotor leftMotor, NXTRegulatedMotor rightMotor, UltrasonicSensor us) {
        this(leftMotor, rightMotor, DEFAULT_WIDTH, DEFAULT_WHEEL_RADIUS);
        this.us = us;
    }
	
	public TwoWheeledRobot(NXTRegulatedMotor leftMotor, NXTRegulatedMotor rightMotor, double width) {
		this(leftMotor, rightMotor, width, DEFAULT_WHEEL_RADIUS);
	}
    
    public int convertDistanceToMotorRotation (double distance){
        return (int) ((180.0 * distance) / (Math.PI * wheelRadius));
    }

    public double getWheelRadius(){
        return this.wheelRadius;
    }

    public double getWidth() {
        return width;
    }

    public int convertAngleToMotorRotation (double angle){
        return convertDistanceToMotorRotation(wheelRadius, width * angle / 2);
    }

    /**
     * Check if the robot is travelling
     * @return is the robot travelling
     */
    public boolean isNavigating(){

        return leftMotor.isMoving() || rightMotor.isMoving();


    }

    public void stop(){
        leftMotor.stop();
        rightMotor.stop();
    }
	// accessors
	public double getDisplacement() {
		return (leftMotor.getTachoCount() * wheelRadius +
				rightMotor.getTachoCount() * wheelRadius) *
				Math.PI / 360.0;
	}
	
	public double getHeading() {
		return (leftMotor.getTachoCount() * wheelRadius -
				rightMotor.getTachoCount() * wheelRadius) / width;
	}
	
	public void getDisplacementAndHeading(double [] data) {
		int leftTacho, rightTacho;
		leftTacho = leftMotor.getTachoCount();
		rightTacho = rightMotor.getTachoCount();
		
		data[0] = (leftTacho * wheelRadius + rightTacho * wheelRadius) *	Math.PI / 360.0;
		data[1] = (leftTacho * wheelRadius - rightTacho * wheelRadius) / width;
	}
	
	// float both motors
	public void setFloat() {
		this.leftMotor.stop();
		this.rightMotor.stop();
		this.leftMotor.flt(true);
		this.rightMotor.flt(true);
	}

	public void setForwardSpeed(double speed) {
		forwardSpeed = speed;
		setSpeeds(forwardSpeed, rotationSpeed);
	}
	
	public void setRotationSpeed(double speed) {
		rotationSpeed = speed;
		setSpeeds(forwardSpeed, rotationSpeed);
	}
	
	public void setSpeeds(double forwardSpeed, double rotationalSpeed) {
		double leftSpeed, rightSpeed;

		this.forwardSpeed = forwardSpeed;
		this.rotationSpeed = rotationalSpeed; 

		leftSpeed = (forwardSpeed + rotationalSpeed * width * Math.PI / 360.0) *
				180.0 / (wheelRadius * Math.PI);
		rightSpeed = (forwardSpeed - rotationalSpeed * width * Math.PI / 360.0) *
				180.0 / (wheelRadius * Math.PI);

		// set motor directions
		if (leftSpeed > 0.0)
			leftMotor.forward();
		else {
			leftMotor.backward();
			leftSpeed = -leftSpeed;
		}
		
		if (rightSpeed > 0.0)
			rightMotor.forward();
		else {
			rightMotor.backward();
			rightSpeed = -rightSpeed;
		}
		
		// set motor speeds
		if (leftSpeed > 900.0)
			leftMotor.setSpeed(900);
		else
			leftMotor.setSpeed((int)leftSpeed);
		
		if (rightSpeed > 900.0)
			rightMotor.setSpeed(900);
		else
			rightMotor.setSpeed((int)rightSpeed);
	}

	public NXTRegulatedMotor[] getMotors() {
		return new NXTRegulatedMotor[]{ leftMotor, rightMotor };
	}
}
