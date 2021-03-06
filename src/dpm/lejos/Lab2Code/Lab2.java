package dpm.lejos.Lab2Code;

/*
 * Lab2.java
 */
import lejos.nxt.*;
import lejos.nxt.comm.RConsole;

public class Lab2 {
    private static double WHEEL_RADIUS = 2.1;
    private static double WHEEL_DISTANCE = 15;
	public static void main(String[] args) {
		//RConsole.openUSB(5000);
        RConsole.println("Connected");
        int buttonChoice;

        ColorSensor colorSensor = new ColorSensor(SensorPort.S1,0);
        // some objects that need to be instantiated
		Odometer odometer = new Odometer(WHEEL_RADIUS, WHEEL_DISTANCE);
		OdometryCorrection odometryCorrection = new OdometryCorrection(odometer, colorSensor);
        OdometryDisplay odometryDisplay = new OdometryDisplay(odometer);

        do {
			// clear the display
			LCD.clear();

			// ask the user whether the motors should drive in a square or float
			LCD.drawString("< Left | Right >", 0, 0);
			LCD.drawString("       |        ", 0, 1);
			LCD.drawString(" Float | Drive  ", 0, 2);
			LCD.drawString("motors | in a   ", 0, 3);
			LCD.drawString("       | square ", 0, 4);

			buttonChoice = Button.waitForAnyPress();
		} while (buttonChoice != Button.ID_LEFT
				&& buttonChoice != Button.ID_RIGHT);

		if (buttonChoice == Button.ID_LEFT) {
			for (NXTRegulatedMotor motor : new NXTRegulatedMotor[] { Motor.A, Motor.B, Motor.C }) {
				motor.forward();
				motor.flt();
			}

			// start only the odometer and the odometry display
			odometer.start();
			odometryDisplay.start();
		} else {
			// start the odometer, the odometry display and (possibly) the
			// odometry correction
			odometer.start();
			odometryDisplay.start();
			odometryCorrection.start();

			// spawn a new Thread to goAround SquareDriver.drive() from blocking
			(new Thread() {
				public void run() {
					SquareDriver.drive(Motor.A, Motor.B, WHEEL_RADIUS, WHEEL_RADIUS, WHEEL_DISTANCE);
				}
			}).start();
		}
		
		Button.waitForAnyPress();
        System.exit(0);
	}
}