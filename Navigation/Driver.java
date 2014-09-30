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
        RConsole.openUSB(30000);

        RConsole.println("Connected");
        UltrasonicSensor usSensor = new UltrasonicSensor(usPort);
        Odometer odometer = new Odometer(WHEEL_RADIUS, WHEEL_DISTANCE);
        OdometryDisplay odometryDisplay = new OdometryDisplay(odometer);
        DriveControl driveControl = new DriveControl(odometer, leftMotor, rightMotor, WHEEL_DISTANCE, WHEEL_RADIUS);
        UltrasonicPoller usPoller = new UltrasonicPoller(usSensor, driveControl);

        usPoller.start();
        odometer.start();
        odometryDisplay.start();
        driveControl.start();

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
