import lejos.nxt.*;
import lejos.nxt.comm.RConsole;

/**
 * Simple test program for driving the NXT in a straight line and connecting to a RConsole on PC
 */
public class motorStraight {

    private NXTRegulatedMotor motorA = Motor.A, motorC = Motor.C;

    public void main(String [] args) {
        RConsole.openUSB(20000);
        motorA.setSpeed(100);
        motorC.setSpeed(100);
        motorA.forward();
        motorC.forward();
        LCD.clear();
        LCD.drawString("Speed: A->"+String.valueOf(motorA.getSpeed()), 0, 0);
        LCD.drawString("Speed: C->"+String.valueOf(motorC.getSpeed()), 0, 1);
        RConsole.println("Speed: A->"+String.valueOf(motorA.getSpeed())+" C->"+String.valueOf(motorC.getSpeed()));
        Button.waitForAnyEvent(30000);
    }
}
