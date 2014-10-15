/**
 * Main executable for lab 5
 * @author Daniel Macario
 * @author David Lavoie-Boutin
 */
import lejos.nxt.*;
import lejos.nxt.comm.RConsole;
import lejos.nxt.comm.USBConnection;

public class Lab5 {

    public static void main (String [] argv){
        int buttonChoice;

        UltrasonicSensor us = new UltrasonicSensor(SensorPort.S2);
        NXTRegulatedMotor leftMotor = Motor.A;
        NXTRegulatedMotor rightMotor = Motor.B;
        double WHEEL_RADIUS = 2.1;
        double WHEEL_DISTANCE = 15;

        DeterministicLocalization dl = new DeterministicLocalization(us, leftMotor, rightMotor, WHEEL_DISTANCE, WHEEL_RADIUS );
        
        do {

            LCD.clear();
            LCD.drawString("< Left | Right >", 0, 0);
            LCD.drawString("       |        ", 0, 1);
            LCD.drawString(" deter |  rando ", 0, 2);
            LCD.drawString("   A   |    B   ", 0, 3);
            LCD.drawString("       |        ", 0, 4);

            buttonChoice = Button.waitForAnyPress();
        } while (buttonChoice != Button.ID_LEFT
                && buttonChoice != Button.ID_RIGHT);

        if (buttonChoice == Button.ID_LEFT) {

           
           dl.deterministicPositioning();

        } else {

            dl.stochasticPositioning();

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
