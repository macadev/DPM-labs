package dpm.lejos.Lab4Code;

import lejos.nxt.UltrasonicSensor;

import java.util.LinkedList;

public class USLocalizer {
	public enum LocalizationType { FALLING_EDGE, RISING_EDGE }
	public static double ROTATION_SPEED = 60;
    private static int US_MOVING_AVERAGE_SIZE = 3;
    private static double WALL_THRESHOLD = 40; //TODO: Value to change

	private Odometer odometer;
	private TwoWheeledRobot robot;
	private UltrasonicSensor us;
	private LocalizationType locType;

    private LinkedList<Integer> usMovingFilter = new LinkedList<Integer>();
	
	public USLocalizer(Odometer odo, UltrasonicSensor us, LocalizationType locType) {
		this.odometer = odo;
		this.robot = odo.getTwoWheeledRobot();
		this.us = us;
		this.locType = locType;
		
		// switch off the ultrasonic sensor
		us.off();
	}
    public static int sum (LinkedList < Integer > list) {
        int sum =0;
        for(int x:list){
            sum += x;
        }
        return sum;
    }
    public static double average (LinkedList<Integer> list){
        return sum(list)/(double)list.size();
    }

	public void doLocalization() {
		double angleA, angleB;
        for (int i = 0; i<US_MOVING_AVERAGE_SIZE; i++){
            usMovingFilter.add(getFilteredData());
        }

        if (locType == LocalizationType.FALLING_EDGE) {
            //we are facing away from the wall


            if (average(usMovingFilter) > WALL_THRESHOLD){
                //indeed facing away from the wall
                robot.setSpeeds(0,ROTATION_SPEED);
                robot.leftMotor.rotate(robot.convertAngleToMotorRotation(2*Math.PI), true);
                robot.rightMotor.rotate(-robot.convertAngleToMotorRotation(2*Math.PI), true);

                while (average(usMovingFilter) > WALL_THRESHOLD){
                    usMovingFilter.remove(0);
                    usMovingFilter.add(getFilteredData());
                }

                robot.stop();

                angleA = odometer.getTheta();
                robot.leftMotor.rotate(-robot.convertAngleToMotorRotation(2*Math.PI), true);
                robot.rightMotor.rotate(robot.convertAngleToMotorRotation(2*Math.PI), true);

                while (average(usMovingFilter) < WALL_THRESHOLD){
                    usMovingFilter.remove(0);
                    usMovingFilter.add(getFilteredData());
                }

                while (average(usMovingFilter) > WALL_THRESHOLD){
                    usMovingFilter.remove(0);
                    usMovingFilter.add(getFilteredData());
                }
                robot.stop();
                angleB = odometer.getTheta();

            } else {
                locType = LocalizationType.RISING_EDGE;
                doLocalization();
                return;
            }

		} else {
            if (average(usMovingFilter) < WALL_THRESHOLD) {
                //indeed facing the wall
                robot.setSpeeds(0, ROTATION_SPEED);
                robot.leftMotor.rotate(robot.convertAngleToMotorRotation(2 * Math.PI), true);
                robot.rightMotor.rotate(robot.convertAngleToMotorRotation(2 * Math.PI), true);

                while (average(usMovingFilter) > WALL_THRESHOLD) {
                    usMovingFilter.remove(0);
                    usMovingFilter.add(getFilteredData());
                }

                robot.stop();

                angleA = odometer.getTheta();
                robot.leftMotor.rotate(robot.convertAngleToMotorRotation(2 * Math.PI), true);
                robot.rightMotor.rotate(robot.convertAngleToMotorRotation(2 * Math.PI), true);

                while (average(usMovingFilter) > WALL_THRESHOLD) {
                    usMovingFilter.remove(0);
                    usMovingFilter.add(getFilteredData());
                }

                while (average(usMovingFilter) < WALL_THRESHOLD) {
                    usMovingFilter.remove(0);
                    usMovingFilter.add(getFilteredData());
                }
                robot.stop();
                angleB = odometer.getTheta();

            } else {
                locType = LocalizationType.FALLING_EDGE;
                doLocalization();
                return;
            }
        }


        double deltaTheta = angleA < angleB ? 45-(angleA+angleB)/2 : 225-(angleA+angleB)/2; //TODO: 45, 225 should be determined experimentally
        odometer.setTheta(odometer.getTheta()+deltaTheta);

	}
	
	private int getFilteredData() {
		int distance;
		
		// do a ping
		us.ping();
		
		// wait for the ping to complete
		try { Thread.sleep(50); } catch (InterruptedException e) {e.printStackTrace();}
		
		// there will be a delay here
		distance = us.getDistance();
				
		return distance;
	}

}
