package dpm.lejos.Lab4Code;

import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.*;

public class Navigation {
	
	// Declaring class variables

	// Wheel_radius and Wheel_distance are needed for our turnTo() method
	// it is part of the calculations to make it turn to the right angle
	private static final double WHEEL_RADIUS = 2.045; 
	private static final double WHEEL_DISTANCE = 15.4;
	
	// Constant speeds for the forward motions
	final static int FAST = 200, SLOW = 100, ACCELERATION = 4000;
	
	// Errors for the angle deviation and the error allowed when traveling to
	// a certain point
	final static double DEG_ERR = 5.0, CM_ERR = 1.0, ANG_ERROR = 10.0;
	
	private Odometer odometer;
	private NXTRegulatedMotor leftMotor, rightMotor;

	public Navigation(Odometer odo) {
		this.odometer = odo;
		
		// Initializing the left and right motors to access them.
		
		NXTRegulatedMotor[] motors = new NXTRegulatedMotor[2];
		leftMotor = Motor.A;
		rightMotor = Motor.B;
		motors[0] = leftMotor;
		motors[1] =  rightMotor;

		// set acceleration
		this.leftMotor.setAcceleration(ACCELERATION);
		this.rightMotor.setAcceleration(ACCELERATION);
	}

	/*
	* Functions to set the motor speeds jointly (Provided code by TA)
	*/
	public void setSpeeds(float lSpd, float rSpd) {
		this.leftMotor.setSpeed(lSpd);
		this.rightMotor.setSpeed(rSpd);
		if (lSpd < 0)
			this.leftMotor.backward();
		else
			this.leftMotor.forward();
		if (rSpd < 0)
			this.rightMotor.backward();
		else
			this.rightMotor.forward();
	}

	// Sets the motor speeds (Provided code by the TA)
	public void setSpeeds(int lSpd, int rSpd) {
		this.leftMotor.setSpeed(lSpd);
		this.rightMotor.setSpeed(rSpd);
		if (lSpd < 0)
			this.leftMotor.backward();
		else
			this.leftMotor.forward();
		if (rSpd < 0)
			this.rightMotor.backward();
		else
			this.rightMotor.forward();
	}

	/*
	 * Float the two motors jointly (Provided code by the TA)
	 */
	public void setFloat() {
		this.leftMotor.stop();
		this.rightMotor.stop();
		this.leftMotor.flt(true);
		this.rightMotor.flt(true);
	}

	/*
	 * Travel to specified x,y coordinates. 
	 * The heading will keep updating itself
	 */
	public void travelTo(double x, double y) {
		double deltaX, deltaY, angle, travelDistance;
		int attempts = 0;
		
	    //Calculate the magnitude of the vector we will need to travel
	    deltaX = x - odometer.getX();
	    deltaY = y - odometer.getY();
	    travelDistance = Math.sqrt(deltaX*deltaX + deltaY*deltaY);
	             
	    //Compute the direction of heading
	    angle = Math.atan2(deltaX, deltaY) * 180 / Math.PI;
	    
	    //Angle correction
	    while (Math.abs(odometer.getTheta() - angle) > DEG_ERR && attempts < 10) {
	        attempts = attempts + 1;
	        turnTo(angle, true);
	        Sound.beep();
	    }
	             
	    leftMotor.setSpeed(SLOW);
	    rightMotor.setSpeed(SLOW);
	             
	    //traverse the magnitude odf the vector
	    Motor.A.rotate(convertDistance(WHEEL_RADIUS, travelDistance), true);
	    Motor.B.rotate(convertDistance(WHEEL_RADIUS, travelDistance), false);
	    
	    Motor.A.stop();
	    Motor.B.stop();
	}

	/*
	 * The original turnTo() method was altered. We implemented our own turnTo() method from Lab 3: Navigation
	 * The turnTo method rotates the robot at the specified angle that is passed through as an argument
	 * In the most optimal way. I.e minimal angle
	 */
	public void turnTo(double turnAngle, boolean stop) {        
	       
		//Get the angle the robot needs to turn  
        double angleToTravel = (turnAngle - odometer.getTheta()) % 360;  
            
        //Get the minimal angle
        if ((angleToTravel > 180)||(angleToTravel < -180)) {
            if (angleToTravel > 180) {
                angleToTravel = angleToTravel - 360;
            } 
            else {
                angleToTravel = angleToTravel + 360;
            }
        }
        
        // if the angle is not within the given error margin
        if (Math.abs(angleToTravel) > DEG_ERR) {
                
            // move clockwise if the angle change is positive
            if (angleToTravel > 0){
                Motor.A.setSpeed(SLOW);
                Motor.B.setSpeed(SLOW);                    
                Motor.A.rotate(convertAngle(WHEEL_RADIUS, WHEEL_DISTANCE, Math.abs(angleToTravel)), true);
                Motor.B.rotate(-convertAngle(WHEEL_RADIUS, WHEEL_DISTANCE, Math.abs(angleToTravel)), false);
             // if angle is negative move counterclockwise
            } else {        	
            	Motor.A.setSpeed(SLOW);
                Motor.B.setSpeed(SLOW);                    
                Motor.A.rotate(-convertAngle(WHEEL_RADIUS, WHEEL_DISTANCE, Math.abs(angleToTravel)), true);
                Motor.B.rotate(convertAngle(WHEEL_RADIUS, WHEEL_DISTANCE, Math.abs(angleToTravel)), false);
            }
        }
         
        //Stop the motors after we turned the amount we desired
        Motor.B.stop();
        Motor.A.stop();
    }
	
	
	/*
	 * Convert distance to wheel rotations
	 */
	private int convertDistance(double radius, double travelDis) {
        return (int) ((180.0 * travelDis) / (Math.PI * radius));
    }
	
	/*
	 * Convert angle to wheel rotations
	 */
	private int convertAngle(double radius, double width, double angle) {
        return convertDistance(radius, Math.PI * width * angle / 360.0);
    }
	
	/*
	 * Go forward a set distance in cm (Code provided by TA)
	 */
	public void goForward(double distance) {
		this.travelTo(Math.cos(Math.toRadians(this.odometer.getTheta())) * distance, Math.cos(Math.toRadians(this.odometer.getTheta())) * distance);

	}
}
