import lejos.nxt.LCD;
import lejos.nxt.UltrasonicSensor;


public class UltrasonicPoller extends Thread{
	private UltrasonicSensor us;
	private DriveControl driver;
	
	public UltrasonicPoller(UltrasonicSensor us, DriveControl driver) {
		this.us = us;
		this.driver = driver;
	}
	
	public void run() {
		while (true) {
			//process collected data
            int distance = us.getDistance();
            LCD.drawString("D: " + distance, 8, 3);
			//driver.avoidObstacleDetection(distance);
			try { Thread.sleep(10); } catch(Exception e){}
		}
	}

}
