import lejos.nxt.Button;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.comm.RConsole;
import lejos.nxt.*;

/**
 * Lab3 main executable
 *
 * @author David id. 260583602
 */
public class Driver {

    private static double WHEEL_RADIUS = 2.1;
    private static double WHEEL_DISTANCE = 15;

    private static NXTRegulatedMotor leftMotor = Motor.A;
    private static NXTRegulatedMotor rightMotor = Motor.B;
    private static final SensorPort usPort = SensorPort.S1;

    public static void main (String [] argv){
        //RConsole.openUSB(30000);

        RConsole.println("Connected");
        UltrasonicSensor usSensor = new UltrasonicSensor(usPort);
        Odometer odometer = new Odometer(WHEEL_RADIUS, WHEEL_DISTANCE);
        OdometryDisplay odometryDisplay = new OdometryDisplay(odometer);
        DriveControl driveControl = new DriveControl(odometer, usSensor, leftMotor, rightMotor, WHEEL_DISTANCE, WHEEL_RADIUS);

        int buttonChoice;

        odometer.start();
        do {
            // clear the display
            LCD.clear();

            // ask the user whether the motors should drive in a square or float
            LCD.drawString("< Left | Right >", 0, 0);
            LCD.drawString("       |        ", 0, 1);
            LCD.drawString(" Part  |  Part  ", 0, 2);
            LCD.drawString("   A   |    B   ", 0, 3);
            LCD.drawString("       |        ", 0, 4);

            buttonChoice = Button.waitForAnyPress();
        } while (buttonChoice != Button.ID_LEFT
                && buttonChoice != Button.ID_RIGHT);

        if (buttonChoice == Button.ID_LEFT) {
            odometryDisplay.start();

            driveControl.travelTo(60, 30);
            driveControl.travelTo(30, 30);
            driveControl.travelTo(30, 60);
            driveControl.travelTo(60, 0);
        } else {
            odometryDisplay.start();

            driveControl.travelTo(0, 60);
            RConsole.println("Finished first move");
            driveControl.travelTo(60, 0);
            RConsole.println("Finished second move");
        }

        while (Button.waitForAnyPress()!=Button.ID_ESCAPE){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.exit(0);
    }
}
