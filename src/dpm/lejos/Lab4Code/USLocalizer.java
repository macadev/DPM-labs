package dpm.lejos.Lab4Code;/* Lab 4: Localization
 * 
 * File: USLocalizer.java
 * 
 * ECSE-211: Design Principles and Methods
 * 
 * Students: Luke Soldano & Tuan-Anh Nguyen
 * 
 * This class is responsible for performing the ultrasonic sensor localization. It will begin
 * facing the wall then will turn until it doesn't face the wall, then keep turning until it sees the wall.
 * It will then turn to each wall closest to it to determine its x and y position. This determines the current
 * x and y position in relation to the origin at 0,0
 * 
 */

import lejos.nxt.*;
import lejos.nxt.comm.RConsole;

public class USLocalizer {
	public enum LocalizationType { 
		FALLING_EDGE, RISING_EDGE
	}
	
	public static double ROTATION_SPEED = 30;
	private Odometer odo;
	private TwoWheeledRobot robot;
	private UltrasonicSensor us;
	private LocalizationType locType;
	
	//distance from wall
	private double distance;
	//previous distance from wall
	private double lastDistance;

	public USLocalizer(Odometer odo, UltrasonicSensor us,
			LocalizationType locType) {
		this.odo = odo;
		this.robot = odo.getTwoWheeledRobot();
		this.us = us;
		this.locType = locType;

		// switch off the ultrasonic sensor
		us.off();
	}

	public void doLocalization() {
		double angleA = 0; 
		double angleB = 0;
		double deltaTheta = 0;
		int initialDistance = getFilteredData();
		
		//If robot is facing corner initially, turn away from it
		while (initialDistance < 50) {
			robot.setRotationSpeed(ROTATION_SPEED);
			initialDistance = getFilteredData();
		}
		
		//Boolean statement to decide if using FALLING_EDGE or RISING_EDGE
		if (locType == LocalizationType.FALLING_EDGE) {
			
			distance = getFilteredData();
			lastDistance = distance;
			
			//rotate away from the wall
			while(distance < 50) {
				robot.setRotationSpeed(ROTATION_SPEED);
				distance = getFilteredData();
			}
			
			sleep();
			
			//rotate towards the wall
			while (distance > 50) {
				robot.setRotationSpeed(ROTATION_SPEED);
				distance = getFilteredData();
			}
			
			//record the first measured angle
			angleB = odo.getTheta();
			robot.stop();
			Sound.beep();
			lastDistance = distance;
			
			sleep();
			
			//rotate away from the wall
			while(distance < 50) {
				robot.setRotationSpeed(-ROTATION_SPEED);
				distance = getFilteredData();
			}
			
			sleep();
			
			//rotate towards the wall
			while(distance > 50) {
				robot.setRotationSpeed(-ROTATION_SPEED);
				distance = getFilteredData();
			}
			
			//record the second measured angle
			angleA = odo.getTheta();
			robot.stop();
			Sound.beep();
			
			//calculate the theta to position parallel to the y axis
			if (angleA > angleB){
				//225
			    deltaTheta = 230- ((angleA + angleB)/2);  
			}else{
				//45
				deltaTheta = 50 - ((angleA + angleB)/2);   
			}
			
			// Calculates correction to theta
			double correctTheta = odo.getTheta() + deltaTheta;
			   
			// Updates the  odometer
			odo.setPosition(new double [] {0.0, 0.0, /*odo.getTheta() - correctTheta*/ 0}, new boolean [] {true, true, true});
			Sound.beep();   
			
			// Robot rotates to 0 degrees, facing towards the +y axis
			robot.rotate(Odometer.minimumAngleFromTo(correctTheta, 0));
			
			// Move the robot in position to detect the four gridlines,
			//It will allow the robot to calculate its position
			positionForLightLocalization();
			odo.setPosition(new double [] {0.0, 0.0, 0.0}, new boolean [] {true, true, true});
			
		} else {
			distance = getFilteredData();  
			lastDistance = distance;
			
			//Rotate robot until it detects a wall
			while(distance > 50) {    
				robot.setRotationSpeed(ROTATION_SPEED);
				distance = getFilteredData();
			}
			
			sleep(); 
						   
			//Rotate the robot until it doesn't see a wall
			while(distance < 50) {
				robot.setRotationSpeed(ROTATION_SPEED);
				distance = getFilteredData();
			}
						
			//record the first angle used for correction
			angleA = odo.getTheta();
			robot.stop();
			Sound.beep();
			lastDistance = distance;
			
			sleep(); 
						
			//rotate robot until it detects a wall
			while(distance > 50){
				robot.setRotationSpeed(-ROTATION_SPEED);
				distance = getFilteredData();
			}
						
			sleep(); 
						   
			//rotate away from the wall until it detects the opposite one
			while (distance < 50){
				robot.setRotationSpeed(-ROTATION_SPEED);
				distance = getFilteredData();
			}
						   
			//record second angle used for correction
			angleB = odo.getTheta();
			robot.stop();
			Sound.beep();
			
			//calculate the angle used to position the robot parallel to the y axis
			if (angleA > angleB){
				deltaTheta = 222 - ((angleA + angleB)/2);  
			}else{
				deltaTheta = 45 - ((angleA + angleB)/2);   
			}
						   
			double correctTheta = odo.getTheta() + deltaTheta;
						   
			odo.setPosition(new double [] {0.0, 0.0, 0.0}, new boolean [] {true, true, true});
			Sound.beep();
			
			// robot moves where it can detect all four grid lines
			
			// Robot rotates to 0 degrees (faces north ~ Parallel to the positive y axis)
			robot.rotate(Odometer.minimumAngleFromTo(correctTheta, 0));
			
			odo.setPosition(new double [] {0.0, 0.0, 0.0}, new boolean [] {true, true, true});
						
			// Robot moves forward six centimeters, rotates 90 degrees and then moves forward 14 centimeters
			// This locates it in an ideal position for the light sensor localization
			
			//robot.goForward(6);
			//robot.stop();
			//robot.rotate(Odometer.minimumAngleFromTo(0, 90));
			//robot.goForward(8);
			//Sound.buzz();
		}
		
	}
	
	public void positionForLightLocalization(){
		robot.goForward(6);
		robot.stop();
		robot.rotate(Odometer.minimumAngleFromTo(0, 90));
		robot.goForward(8);
	}
	
	public void sleep() {
		try { Thread.sleep(1000); } catch (InterruptedException e) {};
	}
	

	// This is a getting to get the filtered ultrasonic sensor (used for display purposes)
	public int getData() {
		return getFilteredData();
	}

	// This is the filter for the ultrasonic sensor. It returns a filtered sensor reading
	
	private int getFilteredData() {
		int distance;
		int[] dist = new int[5];
		for (int i = 0; i < 5; i++) {
			us.ping();

			// wait for ping to complete
			try { Thread.sleep(50); } catch (InterruptedException e) {}

			// there will be a delay
			dist[i] = us.getDistance();

		}

		// sort the array to take the median
		// take values in the array sequentially
		findMedian(dist);
		// take the middle value in the array which is the median and return it
		distance = dist[2];
				
		return distance;
	}
	
	private void findMedian(int[] dist) {
		// sort the array to take the median
		// take values in the array sequentially
		for (int j = 0; j < 5; j++) {
			int min = dist[j];
			int pos = j;

			// find the min value in the remaining part of the array
			for (int k = j; k < 5; k++) {
				if (dist[k] < min) {
					min = dist[k];
					pos = k;
				}
			}

			// set the first position of the unsorted array to the min
			int temp = dist[j];
			dist[j] = min;
			dist[pos] = temp;

		}
	}

}
