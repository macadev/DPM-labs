import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;

/**
 * This class drives the robot around following a specified path
 *
 * @author David id. 260583602
 */
public class Driver extends Thread{

    public static enum AvailablePath {PART_A, PART_B}
    private AvailablePath choice;

    //Drive system parameters
    private static double WHEEL_RADIUS = 2.1;
    private static double WHEEL_DISTANCE = 15;

    //NXT Components
    private static NXTRegulatedMotor leftMotor = Motor.A;
    private static NXTRegulatedMotor rightMotor = Motor.B;
    private static final SensorPort usPort = SensorPort.S1;

    /**
     * Default constructor
     * @param availablepaths Requires the type of path to run
     */
    public Driver (AvailablePath availablepaths){
        choice = availablepaths;
    }

    /**
     * Executable method
     */
    public void run(){

        UltrasonicSensor usSensor = new UltrasonicSensor(usPort);
        Odometer odometer = new Odometer(WHEEL_RADIUS, WHEEL_DISTANCE);
        OdometryDisplay odometryDisplay = new OdometryDisplay(odometer);
        DriveControl driveControl = new DriveControl(odometer, usSensor, leftMotor, rightMotor, WHEEL_DISTANCE, WHEEL_RADIUS);

        odometer.start();
        odometryDisplay.start();


        if (choice == AvailablePath.PART_A){
            driveControl.travelTo(60, 30);
            driveControl.travelTo(30, 30);
            driveControl.travelTo(30, 60);
            driveControl.travelTo(60, 0);
        }
        else if (choice == AvailablePath.PART_B){
            driveControl.travelTo(0, 60);
            driveControl.travelTo(60, 0);

        }
    }

}
