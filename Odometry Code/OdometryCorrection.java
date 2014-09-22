/* 
 * OdometryCorrection.java
 */
import lejos.nxt.*;

import lejos.nxt.LightSensor;

public class OdometryCorrection extends Thread {
    private double offset = 4.5;
	private static final long CORRECTION_PERIOD = 10;
	private Odometer odometer;
    private ColorSensor colorSensor;
    private int light;
    private static final double THETA_THRESHOLD = 3;
    private static final int LIGHT_THRESHOLD = 500; //TODO: Tweak this value

	// constructor
	public OdometryCorrection(Odometer odometer, ColorSensor colorSensor) {
		this.odometer = odometer;
        this.colorSensor = colorSensor;
	}

	// run method (required for Thread)
	public void run() {
		long correctionStart, correctionEnd;

        double[] position = new double[3];
        double orientation;
        double x,y;
		while (true) {
			correctionStart = System.currentTimeMillis();
            light = colorSensor.getNormalizedLightValue();
            //this checks if we are on a line
            if (light < LIGHT_THRESHOLD) {
                odometer.getPosition(position, new boolean[]{true, true, true});
                Sound.beep();
                /*orientation = position[2];

                if (Math.abs(orientation) < THETA_THRESHOLD) {
                    //rotated 0 degrees, traveling in +y
                    y = position[1] + offset;
                    double frac = y /15.0;
                    double disIncrement = Math.abs(frac%1)<0.5 ?  Math.floor(frac) : Math.ceil(frac);
                    odometer.setY(disIncrement*15-offset);
                } else if (Math.abs(orientation + 90) < THETA_THRESHOLD) {
                    //rotated 90 degrees, traveling in -x
                    x = position[0] - offset;
                    double frac = x /15.0;
                    double disIncrement = Math.abs(frac%1)<0.5 ?  Math.floor(frac) : Math.ceil(frac);
                    odometer.setX(disIncrement*15-offset);
                } else if (Math.abs(orientation + 180) < THETA_THRESHOLD) {
                    //rotated 180 degrees, traveling in -y
                    y = position[1] - offset;
                    double frac = y /15.0;
                    double disIncrement = Math.abs(frac%1)<0.5 ?  Math.floor(frac) : Math.ceil(frac);
                    odometer.setY(disIncrement*15-offset);
                } else if (Math.abs(orientation + 270) < THETA_THRESHOLD) {
                    //rotated 270 degrees, traveling in +x
                    x = position[0] + offset;
                    double frac = x /15.0;
                    double disIncrement = Math.abs(frac%1)<0.5 ?  Math.floor(frac) : Math.ceil(frac);
                    odometer.setX(disIncrement*15-offset);
                }*/
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
}