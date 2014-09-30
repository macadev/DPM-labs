/*
 * OdometryCorrection.java
 */
import lejos.nxt.*;

public class OdometryCorrection extends Thread {
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


        double[] position = new double[3];
        double orientation;
       	while (true) {
			correctionStart = System.currentTimeMillis();
			double valX = odometer.getX();
			double valY = odometer.getY();

            light = colorSensor.getNormalizedLightValue();
            //this checks if we are on a line and only count each line once
            if (light < LIGHT_THRESHOLD) {
                odometer.getPosition(position, new boolean[]{true, true, true});

                line_count++;

                orientation = position[2];

                if (Math.abs(orientation) < THETA_THRESHOLD) {
                    //rotated 0 degrees, traveling in +y

                	//Snap distance to the closest line
                	odometer.setY(closestLine(valY, orientation));
                	
                } else if (Math.abs(orientation + 90) < THETA_THRESHOLD) {
                    //rotated 90 degrees, traveling in +x

                    //Snap distance to the closest line
                    odometer.setX(closestLine(valX, orientation));
                	
                } else if (Math.abs(orientation + 180) < THETA_THRESHOLD) {
                    //rotated 180 degrees, traveling in -y

                    //Snap distance to the closest line
                    odometer.setY((closestLine(valY, orientation)));
                	
                 } else if (Math.abs(orientation + 270) < THETA_THRESHOLD) {
                    //rotated 270 degrees, traveling in -x

                    //Snap distance to the closest line
                    odometer.setX(closestLine(valX, orientation));
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

    /**
     * compute the closest line to the distance provided
     * @param distance distance you have when crossing the line
     * @return position of the line you crossed
     */
    public double closestLine( double distance, double angle){
        double offset = 5;
        if (Math.abs(angle) < 150) {
            if (distance < 30) {
                //we know it is the first line we cross in that direction
                return 15 - offset;
            }
            //sensor is farther from the origin than the center of the robot
            return Math.round(((distance + offset - 15) / 30)) * 30 + 15 - offset;
        }
        //sensor is closer to the origin than the center of the robot
        return Math.round(((distance - offset - 15) / 30)) * 30 + 15 + offset;
    }
    public int getLight (){
        return light;
    }

    public int getLineCount() {
        return line_count;
    }
}