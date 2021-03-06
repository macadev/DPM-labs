package dpm.lejos.Lab4Code;

import lejos.nxt.LCD;
import lejos.util.Timer;
import lejos.util.TimerListener;

public class LCDInfo implements TimerListener{
    public static final int LCD_REFRESH = 100;
    private Odometer odo;
    private Timer lcdTimer;
    private static double angleA = 0;
    private static double angleB = 0;

    // arrays for displaying data
    private double [] pos;

    public LCDInfo(Odometer odo) {
        this.odo = odo;
        this.lcdTimer = new Timer(LCD_REFRESH, this);

        // initialise the arrays for displaying data
        pos = new double [3];

        // start the timer
        lcdTimer.start();
    }

    public void timedOut() {
        odo.getPosition(pos);
        LCD.clear();
        LCD.drawString("X: ", 0, 0);
        LCD.drawString("Y: ", 0, 1);
        LCD.drawString("H: ", 0, 2);
        LCD.drawInt((int)(pos[0] * 10), 3, 0);
        LCD.drawInt((int)(pos[1] * 10), 3, 1);
        LCD.drawInt((int)pos[2], 3, 2);
        if (angleA!=0)LCD.drawString("AngleA: "+String.valueOf(angleA), 0, 4);
        if (angleB!=0)LCD.drawString("AngleB: "+String.valueOf(angleB), 0, 5);

    }
    public static void printAngleA(double newAngleA){
        angleA = newAngleA;
    }

    public static void printAngleB(double newAngleB){
        angleB = newAngleB;
    }
}
