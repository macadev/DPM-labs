/* 
 * OdometryCorrection.java
 */
import lejos.nxt.*;

import lejos.nxt.LightSensor;

public class OdometryCorrection extends Thread {
    private double offset = 5;
	private static final long CORRECTION_PERIOD = 20;
	private Odometer odometer;
    private ColorSensor colorSensor;
    private int light;
    private static final double THETA_THRESHOLD = 10;
    private static final int LIGHT_THRESHOLD = 500; //TODO: Tweak this value
    private int line_count=0;
	// constructor
	public OdometryCorrection(Odometer odometer, ColorSensor colorSensor) {
		this.odometer = odometer;
        this.colorSensor = colorSensor;
	}

	// run method (required for Thread)
	public void run() {
		long correctionStart, correctionEnd;


        float snapX = 0;
        float snapY = 0;
        double[] position = new double[3];
        double orientation;
        double x,y;
		while (true) {
			correctionStart = System.currentTimeMillis();
			double valX = odometer.getX();
			double valY = odometer.getY();
			snapX = ((int)Math.abs(odometer.getX()))/30+1; 
	        snapY = ((int)Math.abs(odometer.getY()))/30+1;
			
            light = colorSensor.getNormalizedLightValue();
            //this checks if we are on a line and only count each line once
            if (light < LIGHT_THRESHOLD) {
                odometer.getPosition(position, new boolean[]{true, true, true});
                Sound.beep();

                line_count++;

                orientation = position[2];

                if (Math.abs(orientation) < THETA_THRESHOLD) {
                    //rotated 0 degrees, traveling in +y
                	
                	odometer.setY((Math.round(((valY - offset - 15) / 30)) * 30 + 15 + offset));                	
                	
                } else if (Math.abs(orientation + 90) < THETA_THRESHOLD) {
                    //rotated 90 degrees, traveling in -x
                	
                	odometer.setX((Math.round(((valX - offset - 15) / 30)) * 30 + 15 + offset));
                	
                } else if (Math.abs(orientation + 180) < THETA_THRESHOLD) {
                    //rotated 180 degrees, traveling in -y
                	
                	odometer.setY((Math.round(((valY - offset - 15) / 30)) * 30 + 15 + offset));
                	
                 } else if (Math.abs(orientation + 270) < THETA_THRESHOLD) {
                    //rotated 270 degrees, traveling in +x
                	
                	odometer.setX((Math.round(((valX - offset - 15) / 30)) * 30 + 15 + offset));   	
                }
            }

			// this ensures the odometry correction occurs only once every period
			correctionEnd = System.currentTimeMillis();
			if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
				try {
					Thread.sleep(CORRECTION_PERIOD
							- (correctionEnd - correctionStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometry correction will be
					// interrupted by another thread
				}
			}
		}
	}
    public int getLight (){
        return light;
    }

    public int getLineCount() {
        return line_count;
    }
}