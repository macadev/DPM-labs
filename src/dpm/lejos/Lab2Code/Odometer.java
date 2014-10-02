package dpm.lejos.Lab2Code;

/*
 * Odometer.java
 */

import lejos.nxt.Motor;
import lejos.nxt.MotorPort;
import lejos.nxt.comm.RConsole;

/**
 * Odometer class is tasked with keeping track of the robot's position using the tachometers on the servos
 */
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

    /**
     * Empty constructor, usefull for unit tests
     */
    public Odometer (){
        WHEEL_DISTANCE = 15;
        WHEEL_RADIUS =2.1;
        lock = new Object();
    }

    /**
     * Default constructor
     * @param wheel_radius wheel radius of the robot, this assumes both wheels have the same radius
     * @param wheel_distance distance between the two wheels
     */
	public Odometer(double wheel_radius, double wheel_distance) {
		x = 0.0;
		y = 0.0;
		theta = Math.PI/2;
        WHEEL_DISTANCE = wheel_distance;
        WHEEL_RADIUS = wheel_radius;
		lock = new Object();
        Motor.A.resetTachoCount();
        Motor.B.resetTachoCount();
        prevTachoL = 0;
        prevTachoR = 0;
    }

    /**
     * Main runnable method
     *
     * At every loop, pools the tacho, and computes the instantenous displacement and rotation
     * then updates the registers accordingly
     */
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
                x += dCenter * Math.cos(theta);
                y += dCenter * Math.sin(theta);
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

    /**
     * Getter for the global position of the robot
     * @param position array of size 3 which will be updated with the position of the robot {x,y,t}
     * @param update array of 3 booleans indicating which element should be updated
     */
	public void getPosition(double[] position, boolean[] update) {
		// ensure that the values don't change while the odometer is running
		synchronized (lock) {
			if (update[0])
				position[0] = x;
			if (update[1])
				position[1] = y;
			if (update[2])
				position[2] = theta;
		}
	}

    /**
     *
     * @return current x coordinate
     */
	public double getX() {
		double result;

		synchronized (lock) {
			result = x;
		}

		return result;
	}

    /**
     *
     * @return current y coordinate
     */
	public double getY() {
		double result;

		synchronized (lock) {
			result = y;
		}

		return result;
	}

    /**
     *
     * @return current orientation w.r.t. the x axis
     */
	public double getTheta() {
		double result;

		synchronized (lock) {
			result = theta;
        }

		return result;
	}

	// mutators

    /**
     * setter for all the position elements
     * @param position array of 3 elements containing the x and y coordinates and the orientation
     * @param update array of 3 booleans indicating which element should be updated
     */
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

    /**
     *
     * @param x new x coordinate
     */
	public void setX(double x) {
		synchronized (lock) {
			this.x = x;
		}
	}

    /**
     *
     * @param y new y coordinate
     */
	public void setY(double y) {
		synchronized (lock) {
			this.y = y;
		}
	}

    /**
     *
     * @param theta new orientation
     */
	public void setTheta(double theta) {
		synchronized (lock) {
			this.theta = theta;
		}
	}
}