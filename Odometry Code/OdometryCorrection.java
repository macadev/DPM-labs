/* 
 * OdometryCorrection.java
 */

import lejos.nxt.LightSensor;

public class OdometryCorrection extends Thread {
	private static final long CORRECTION_PERIOD = 10;
	private Odometer odometer;
    private LightSensor lightSensor;

    private static final double THETA_THRESHOLD = 3;
    private static final int LIGHT_THRESHOLD = 50; //TODO: Tweak this value

	// constructor
	public OdometryCorrection(Odometer odometer, LightSensor lightSensor) {
		this.odometer = odometer;
        this.lightSensor = lightSensor;
	}

	// run method (required for Thread)
	public void run() {
		long correctionStart, correctionEnd;

        double[] position = new double[3];
        double orientation;
		while (true) {
			correctionStart = System.currentTimeMillis();
            //this checks if we are on a line
            if (lightSensor.getLightValue() < LIGHT_THRESHOLD) {
                odometer.getPosition(position, new boolean[]{true, true, true});
                orientation = position[2];

                if (Math.abs(orientation) < THETA_THRESHOLD) {
                    //rotated 0 degrees, travelling in y
                    double frac = position[1]/15.0;
                    double disIncrement = Math.abs(frac%1)<0.5 ?  Math.floor(frac) : Math.ceil(frac);
                    odometer.setY(disIncrement*15);
                } else if (Math.abs(orientation + 90) < THETA_THRESHOLD) {
                    //rotated 90 degrees, travelling in x
                    double frac = position[1]/15.0;
                    double disIncrement = Math.abs(frac%1)<0.5 ?  Math.floor(frac) : Math.ceil(frac);
                    odometer.setX(disIncrement*15);
                } else if (Math.abs(orientation + 180) < THETA_THRESHOLD) {
                    //rotated 180 degrees, travelling in y
                    double frac = position[1]/15.0;
                    double disIncrement = Math.abs(frac%1)<0.5 ?  Math.floor(frac) : Math.ceil(frac);
                    odometer.setY(disIncrement*15);
                } else if (Math.abs(orientation + 270) < THETA_THRESHOLD) {
                    //rotated 270 degrees, travelling in x
                    double frac = position[0]/15.0;
                    double disIncrement = Math.abs(frac%1)<0.5 ?  Math.floor(frac) : Math.ceil(frac);
                    odometer.setX(disIncrement*15);
                }
            }
			// put your correction code here

			// this ensure the odometry correction occurs only once every period
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
}