/* Lab 4: Localization
 * 
 * File: LightLocalizer.java
 * 
 * ECSE-211: Design Principles and Methods
 * 
 * Students: Luke Soldano & Tuan-Anh Nguyen
 * 
 * The LightLocalizer class is responsible for localizing the robot using only its light sensor
 * The light sensor is very accurate and takes note of the angle at which the robot is at each 
 * time it crosses a black line
 * 
 */


import lejos.nxt.*;

public class LightLocalizer {
	
	// Declaration of class variables. The LightLocalizer will need access to the odometer, robot in motion, and
	// the light sensor the robot is using
	private Odometer odo;
	private TwoWheeledRobot robot;
	private ColorSensor ls;
	//private static final int TIME_THRESHOLD = 100;
	
	// Keeping track of the time when the robot senses a black line to make sure it doesn't count it twice
	private double lastTime = 0;
	private double currentTime;
	// Constant speeds of the robot when it is rotating
	private static final int ROTATIONAL_SPEED = 20;
	private static final double ERROR = 1.0; // An error 
	private int counter = 0; // A counter that keeps track of every time a black line is sensed
	// The angles each time the robot senses a black line
	private double angleA, angleB, angleC, angleD;
	// The distance the sensor distance is from the center of the robot
	public static double SENSOR_DISTANCE = 5.0; 

	
	// Constructor for the light localizer to build it
	public LightLocalizer(Odometer odo, ColorSensor ls) {
		this.odo = odo;
		this.robot = odo.getTwoWheeledRobot();
		this.ls = ls;
		
		// turn on the light
		ls.setFloodlight(true);
	}
	
	// The method that actually does light localization. The robot will be at a point close to the origin
	// defined to be 0,0. It will "spin" 360 degrees and will detect a 4 black lines during its circular path
	// Using the angles latched, the current position of the robot can be re-calculated (after ultrasonic sensor
	// localization). It is much more precise than the ultrasonic sensor
	
	public void doLocalization() {
		
		// Passing through the current odometer to the navigation 
		Navigation nav = new Navigation(this.odo);
		// Keeping track of time to prevent the robot from updating the line counter too much when it senses a line
		currentTime = (double)System.nanoTime();
		
		// Determining the start angle of the robot
		double startAngle = odo.getTheta();
		
		// This for loop makes sure that the angle rotates a bit so that a starting angle can be
		// determined since the robot is orientated at 0 degrees at this point in the demo.
		
		for(int i = 0; i < 250; i++){
			
			robot.setRotationSpeed(-ROTATIONAL_SPEED);
			
		}
		// Tracking time of the robot
		currentTime = System.nanoTime();
		lastTime = currentTime - 200*Math.pow(10,6); // Multiplying the time by 10^6 because time is in nano seconds
		
		// While loop makes sure that the robot keeps rotating until it reaches its original starting angle position
		// which is known from originally rotating it for a certain time. (startAngle variable stores this angle)
		// It ends up rotating in a circle and it is able to track all 4 black lines.
		
		while(Math.abs(startAngle - odo.getTheta()) > ERROR){
			// Keeps track of time
			currentTime = System.nanoTime();
			// Getting the light sensor readings
			double sensorReading = ls.getNormalizedLightValue();
			// Displaying light, counter, and angle information to the screen
			LCD.drawString("Light : " + sensorReading, 0, 2);
			LCD.drawString("Counter : " + counter, 0, 3);
			LCD.drawString("Angle : " + odo.getTheta(), 0, 1);
			
			// This is the condition for sensing a black line. Black lines occur when it is approximately
			// less than 500 so a threshold value used was a reading of 420. Also, the time difference between it
			// senses a black line to the next must be larger 2 seconds. This will make sure that the robot
			// does not increment the black line counter by more than 1 every time it senses a black line because
			// of the frequency of the light sensor
			
			if((sensorReading < 420) && (currentTime - lastTime > (200*Math.pow(10,6)))){
				
			
				lastTime = System.nanoTime();
				counter++;
				
				// The angle is recorded each time the robot detects a black line.
				// Counter corresponds to the nth black line it senses. After sensing 4
				// lines, we know that it has completed a circle and returns to its original
				// position when light localizing started
				
				if(counter == 1) angleA = odo.getTheta();
				if(counter == 2) angleB = odo.getTheta();
				if(counter == 3) angleC = odo.getTheta();
				if(counter == 4) angleD = odo.getTheta();
								
			}
		}	
			// Converting the angles so the math is correct when calculating x and y positions
			if(angleA > 180) angleA = angleA - 180;
			if(angleB > 180) angleB = angleB - 180;
			if(angleC > 180) angleC = angleC - 180;	
			if(angleD > 180) angleD = angleD - 180;
		
			// The robot stops after tracking all 4 angles
			robot.setRotationSpeed(0);
		
			// Calculating the difference in angles of the 1st and 3rd and 2nd and 4th black line
			// to determine the x and y position
			
			double thetaY = angleC - angleA;
			double thetaX = angleD - angleB;
			
			// Calcating the robot/s position based on trignometric equations (provided in the tutorial slides)
			
			double xPosition = -(SENSOR_DISTANCE)*(Math.cos(thetaY/2));
			double yPosition = -(SENSOR_DISTANCE)*(Math.cos(thetaX/2));
			
			LCD.drawString("X:" + xPosition, 1, 4);
			LCD.drawString("Y: " + yPosition, 1, 5);
			
			// Calculating the correction angle of the robot
			double deltaTheta = 180+thetaY/2 - angleD;
			
			// Setting the odometer position (only the angle) after it does the light localization with the correction
			// angle (deltaTheta) taken into consideration
			
			odo.setPosition(
					new double[] { 0.0, 0.0, odo.getTheta() + deltaTheta },
					new boolean[] { false, false, true });
			
			// After the robot has localized, the robot will travel to the origin defined at (0,0)
			
			nav.travelTo(0,0);
			
			// The following lines describe the method of physically adjusting the robot so that it points
			// directly north (0 degrees) with the robot perfectly aligned along the black lines
			// After driving to the origin, the robot will turn one way to correct for the slight
			// deviation in the angle of the robot. It will turn one way for 3 seconds and if it doesn't
			// sense a line, it will turn the other way to 
			
			int coeff = 1;
			double timeStart = System.nanoTime()*Math.pow(10,9);
			while(ls.getNormalizedLightValue() > 450){
				robot.setRotationSpeed(-5*coeff);
				if(System.nanoTime()*Math.pow(10,9)-timeStart > 3) coeff = -2;
			}
			
			// Stop the robot after it has corrected itself and aligned itself to pure north direction
			
			robot.setRotationSpeed(0);
			
			// Setting the position and angle of the robot to all 0's since it is at the origin orientated
			// towards north
			
			odo.setPosition(
					new double[] { 0.0, 0.0, 0.0 },
					new boolean[] { true, true, true });
			}

}
