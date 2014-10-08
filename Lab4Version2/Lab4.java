import lejos.nxt.*;
import lejos.nxt.comm.RConsole;

public class Lab4 {

	public static void main(String[] args) {
		
		TwoWheeledRobot patBot = new TwoWheeledRobot(Motor.A, Motor.B);
		Odometer odo = new Odometer(patBot, 30, true);
		UltrasonicSensor us = new UltrasonicSensor(SensorPort.S2);
		ColorSensor ls = new ColorSensor(SensorPort.S1);
		int button;
		do {
			// Clear the display
			LCD.clear();

			// Ask the user to choose between the two available modes
			LCD.drawString("  fall | rise   ", 0, 0);
			LCD.drawString("  edge | edge   ", 0, 1);
			LCD.drawString("       |        ", 0, 2);
			LCD.drawString("       |        ", 0, 3);
			LCD.drawString("       |        ", 0, 4);

			button = Button.waitForAnyPress();
			
		} while (button != Button.ID_LEFT
				&& button != Button.ID_RIGHT);
		
		// perform the light sensor localization
		
		if (button == Button.ID_LEFT) {
			// FALLING EDGE ultrasonic localization;
			LCDInfo lcd = new LCDInfo(odo);
			USLocalizer usl = new USLocalizer(odo, us, USLocalizer.LocalizationType.FALLING_EDGE);
			usl.doLocalization();
			LightLocalizer lsl  = new LightLocalizer(odo, ls);
			lsl.doLocalization();
		} else {
			//RISING EDGE ultrasonic localization
			LCDInfo lcd = new LCDInfo(odo);
			USLocalizer usl = new USLocalizer(odo, us, USLocalizer.LocalizationType.FALLING_EDGE);
			usl.doLocalization();
			LightLocalizer lsl  = new LightLocalizer(odo, ls);
			lsl.doLocalization();
		}
		
		while (Button.waitForAnyPress() != Button.ID_ESCAPE) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.exit(0);
		}

	}

}
