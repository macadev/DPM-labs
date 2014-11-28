package dpm.lejos.Lab5Code; /**
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

        Orienteering orienteering = new Orienteering(us, leftMotor, rightMotor, WHEEL_DISTANCE, WHEEL_RADIUS );
        
        do {

            LCD.clear();
            LCD.drawString("< Left | Right >", 0, 0);
            LCD.drawString("       |        ", 0, 1);
            LCD.drawString(" deter |  rando ", 0, 2);
            LCD.drawString("   A   |    B   ", 0, 3);
            LCD.drawString("       |        ", 0, 4);

            buttonChoice = Button.waitForAnyPress();
        } while (buttonChoice != Button.ID_LEFT
                && buttonChoice != Button.ID_RIGHT && buttonChoice!= Button.ID_ESCAPE);

        if (buttonChoice == Button.ID_LEFT) {

            //start deterministic Positioning algorithm
            orienteering.deterministicPositioning();

        } else if (buttonChoice == Button.ID_RIGHT) {

            //start stochastic Positioning algorithm
            orienteering.stochasticPositioning();

        } else {
            LCD.clear();

            orienteering.deterministicPositioning();
            Button.waitForAnyPress();
            for (int i = 0; i<10; i++){
                LCD.clear();
                orienteering = new Orienteering(us, leftMotor, rightMotor, WHEEL_DISTANCE, WHEEL_RADIUS );
                orienteering.stochasticPositioning();
                Button.waitForAnyPress();
            }
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
