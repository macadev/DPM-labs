import lejos.nxt.Button;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;

/**
 * @author David id. 260583602
 */
public class Driver {

    private static double WHEEL_RADIUS = 2.1;
    private static double WHEEL_DISTANCE = 15;

    private static NXTRegulatedMotor leftMotor = Motor.A;
    private static NXTRegulatedMotor rightMotor = Motor.B;

    public static void main (String [] argv){

        Odometer odometer = new Odometer(WHEEL_RADIUS, WHEEL_DISTANCE);
        odometer.start();

        OdometryDisplay odometryDisplay = new OdometryDisplay(odometer);
        odometryDisplay.start();

        DriveControl driveControl = new DriveControl(odometer, leftMotor, rightMotor, WHEEL_DISTANCE, WHEEL_RADIUS);
        driveControl.start();

        Button.waitForAnyPress();
        System.exit(0);

    }
}
