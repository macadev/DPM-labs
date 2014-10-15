/**
 * Created by danielmacario on 2014-10-14.
 */

import lejos.nxt.*;
import lejos.nxt.comm.RConsole;
import lejos.nxt.comm.USBConnection;

public class Lab5 {

    public static void main (String [] argv){
        int buttonChoice;
        //RConsole.openUSB(10000);

        //Required Elements:
        //Odometer odo = new Odometer(patBot, 30, true);
        UltrasonicSensor us = new UltrasonicSensor(SensorPort.S2);
        NXTRegulatedMotor leftMotor = Motor.A;
        NXTRegulatedMotor rightMotor = Motor.B;
        DeterministicLocalization dl = new DeterministicLocalization(us, leftMotor, rightMotor );
        
        do {
            // clear the display
            LCD.clear();

            // ask the user whether the motors should drive in a square or float
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
