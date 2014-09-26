/*
 * Odometer.java
 */

import lejos.nxt.Motor;
import lejos.nxt.MotorPort;
import lejos.nxt.comm.RConsole;

public class Odometer extends Thread {
	// robot position
	private double x, y, theta;

    //tacho counts (previous)
    private int prevTachoL, prevTachoR;

    private final double WHEEL_RADIUS ;
    private final double WHEEL_DISTANCE;
	// odometer update period, in ms
	private static final long ODOMETER_PERIOD = 100;

	// lock object for mutual exclusion
	private final Object lock;

	// default constructor
    public Odometer (){
        WHEEL_DISTANCE = 15;
        WHEEL_RADIUS =2.8;
        lock = new Object();
    }
	public Odometer(double wheel_radius, double wheel_distance) {
		x = 0.0;
		y = 0.0;
		theta = 0.0;
        WHEEL_DISTANCE = wheel_distance;
        WHEEL_RADIUS = wheel_radius;
		lock = new Object();
        Motor.A.resetTachoCount();
        Motor.B.resetTachoCount();
        prevTachoL = 0;
        prevTachoR = 0;
    }

	// run method (required for Thread)
	public void run() {
		long updateStart, updateEnd;

		while (true) {
			updateStart = System.currentTimeMillis();
			// put (some of) your odometer code here
	        //Get variation tacho
            int tachoDeltaL = Motor.A.getTachoCount() - prevTachoL;
            int tachoDeltaR = Motor.B.getTachoCount() - prevTachoR;

            //convert tacho counts to travelled distance
            double dLeft = (WHEEL_RADIUS * Math.PI * tachoDeltaL) / 180;
            double dRright = (WHEEL_RADIUS * Math.PI * tachoDeltaR) / 180;
            double dCenter = (dLeft + dRright) /2;

            //use difference in distance to get angle
            double deltaTheta = (dRright - dLeft) / WHEEL_DISTANCE;

            synchronized (lock) {
                //update prev tacho counts
                prevTachoL += tachoDeltaL;
                prevTachoR += tachoDeltaR;

                theta = (theta + deltaTheta) % (2 * Math.PI);

                //update current position
                x += -(dCenter *Math.sin(theta));
                y += dCenter *Math.cos(theta);
			}

			// this ensures that the odometer only runs once every period
			updateEnd = System.currentTimeMillis();
			if (updateEnd - updateStart < ODOMETER_PERIOD) {
				try {
					Thread.sleep(ODOMETER_PERIOD - (updateEnd - updateStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometer will be interrupted by
					// another thread
				}
			}
		}
	}

	// accessors
	public void getPosition(double[] position, boolean[] update) {
		// ensure that the values don't change while the odometer is running
		synchronized (lock) {
			if (update[0])
				position[0] = x;
			if (update[1])
				position[1] = y;
			if (update[2])
				position[2] = theta*180/Math.PI;
		}
	}

	public double getX() {
		double result;

		synchronized (lock) {
			result = x;
		}

		return result;
	}

	public double getY() {
		double result;

		synchronized (lock) {
			result = y;
		}

		return result;
	}

	public double getTheta() {
		double result;

		synchronized (lock) {
			result = theta;
		}

		return result;
	}

	// mutators
	public void setPosition(double[] position, boolean[] update) {
		// ensure that the values don't change while the odometer is running
		synchronized (lock) {
			if (update[0])
				x = position[0];
			if (update[1])
				y = position[1];
			if (update[2])
				theta = position[2];
		}
	}

	public void setX(double x) {
		synchronized (lock) {
			this.x = x;
		}
	}

	public void setY(double y) {
		synchronized (lock) {
			this.y = y;
		}
	}

	public void setTheta(double theta) {
		synchronized (lock) {
			this.theta = theta;
		}
	}
}