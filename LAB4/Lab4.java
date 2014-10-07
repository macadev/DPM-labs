/* Lab 4: Localization
 * 
 * File: Lab4.java (Main class of Lab 4)
 * 
 * ECSE-211: Design Principles and Methods
 * 
 * Students: Luke Soldano & Tuan-Anh Nguyen
 * 
 * This is the main class of Lab 4. It starts the ultrasonic sensor localization and then once that finishes
 * it starts the light localizer to fully localize the robot
 * 
 */
import lejos.nxt.*;

public class Lab4 {

	public static void main(String[] args) {
	
		// setup the odometer, display, and ultrasonic and light sensors
		// also creates the robot to use
		Button.waitForAnyPress();
		TwoWheeledRobot patBot = new TwoWheeledRobot(Motor.A, Motor.B);
		Odometer odo = new Odometer(patBot, 30, true);
		LCDInfo lcd = new LCDInfo(odo);
		UltrasonicSensor us = new UltrasonicSensor(SensorPort.S2);
		ColorSensor ls = new ColorSensor(SensorPort.S1);
		
		// perform the ultrasonic localization
		// The ultrasonic localization has two types: Rising edge and Falling edge
		
		USLocalizer usl = new USLocalizer(odo, us, USLocalizer.LocalizationType.FALLING_EDGE);
		usl.doLocalization();
		
		// performs the light sensor localization
		//LightLocalizer lsl = new LightLocalizer(odo, ls);
		//lsl.doLocalization();
		
		// Press a button to end the main method
		Button.waitForAnyPress();

	}

}
