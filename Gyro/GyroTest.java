import lejos.nxt.*;
import lejos.nxt.addon.GyroDirectionFinder;
import lejos.nxt.addon.GyroSensor;

/**
 * Created by David on 2014-09-18.
 *
 */
public class GyroTest {
    public static void main(String[] args){
        GyroSensor gyro = new GyroSensor(SensorPort.S1);
        NXTRegulatedMotor motor = Motor.A;
        GyroDirectionFinder directionFinder = new GyroDirectionFinder(gyro, true);
        GyroPoller poller = new GyroPoller(directionFinder);
        GyroMotor gyroMotor = new GyroMotor(motor, poller);
        poller.start();
        gyroMotor.start();
        Sound.beepSequenceUp();

        Button.waitForAnyPress();
        System.exit(0);
    }
}