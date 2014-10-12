package dpm.lejos.Lab4Code;/* Lab 4: Localization
 * 
 * File: LightLocalizer.java
 * 
 * ECSE-211: Design Principles and Methods
 * 
 * Students: Luke Soldano & Tuan-Anh Nguyen
 * 
 * The LightLocalizer class is responsible for localizing the robot using only its light sensor
 * The light sensor is very accurate and takes note of the angle at which the robot is at each 
 * time it crosses a black line
 * 
 */


import lejos.nxt.*;

public class LightLocalizer {
	
	private Odometer odo;
	private TwoWheeledRobot robot;
	private ColorSensor ls;
	//private static final int TIME_THRESHOLD = 100;
	
	private int currentLight;
	private int lastLight;
	private int lightBeforeLastLight;
	private int LightD = 60;
	
	private static final int ROTATIONAL_SPEED = 30; 
	
	// Constructor for the light localizer to build it
	public LightLocalizer(Odometer odo, ColorSensor ls) {
		this.odo = odo;
		this.robot = odo.getTwoWheeledRobot();
		this.ls = ls;	
	}
	
	public void doLocalization() {
		//Distance from light center to center of the robot (middle point between wheels)
		double sensorDistance = 12.0;
		ls.setFloodlight(true);
		  
        // read angles for localization trigonometry
        currentLight = ls.getNormalizedLightValue();  
        lastLight = currentLight;
        lightBeforeLastLight = lastLight;
        int lightValue = currentLight; 
        
        // keep rotating if no gridlines are detected 
        while (isNotBlackLine(lightValue, currentLight, lastLight, LightD)){ 
            lightBeforeLastLight = lastLight;
            lastLight = currentLight;
            currentLight = lastLight;
            
            robot.setRotationSpeed(ROTATIONAL_SPEED); 
            lightValue = ls.getNormalizedLightValue();  
        } 
        Sound.beep();
        
        //Save angle for the intersection with the first horizontal line
        double XAxisAngle1 = odo.getTheta();
        
        sleep(); 
      
        lightValue = ls.getNormalizedLightValue();

        // keep rotating if no gridlines are detected
        while (isNotBlackLine(lightValue, currentLight, lastLight, LightD)){ 
            lightBeforeLastLight = lastLight;
            lastLight = currentLight;
            currentLight = lastLight;
            robot.setRotationSpeed(ROTATIONAL_SPEED); 
            
            lightValue = ls.getNormalizedLightValue();  
        } 
        Sound.beep();
        //Save angle for the intersection with the first vertical line
        double YAxisAngle1 = odo.getTheta(); 
        
        sleep(); 
        
        lightValue = ls.getNormalizedLightValue();
        
        // keep rotating if no gridlines are detected 
   	 	while (isNotBlackLine(lightValue, currentLight, lastLight, LightD)){ 
            lightBeforeLastLight = lastLight;
            lastLight = currentLight;
            currentLight = lastLight;
            robot.setRotationSpeed(ROTATIONAL_SPEED); 
            lightValue = ls.getNormalizedLightValue();  
        } 
   	 	Sound.beep();
   	   //Save angle for the intersection with the second horizontal line
       double XAxisAngle2 = odo.getTheta();   
 
       sleep(); 
     
       lightValue = ls.getNormalizedLightValue();
       // keep rotating if no gridlines are detected  
       
  	   while (isNotBlackLine(lightValue, currentLight, lastLight, LightD)){ 
           lightBeforeLastLight = lastLight;
           lastLight = currentLight;
           currentLight = lastLight;
           robot.setRotationSpeed(ROTATIONAL_SPEED); 
           lightValue = ls.getNormalizedLightValue();  
       }
  	   Sound.beep();
  	   
  	   //Save angle for the intersection with the second vertical line
  	   double YAxisAngle2 = odo.getTheta(); // CAPTURE ANGLE
  	   robot.stop(); 
  	   // sleep 
  	   sleep();  
  	   lightValue = ls.getNormalizedLightValue();
      
  	   //calculations to determine position
  	   double thetaX = XAxisAngle2 - XAxisAngle1;  
  	   double thetaY = YAxisAngle2 - YAxisAngle1;  
        
  	   double updatedX = -sensorDistance*Math.cos((Math.toRadians(thetaY/2)));  
  	   double updatedY = -sensorDistance*Math.cos((Math.toRadians(thetaX/2))); 
        
  	   double deltaTheta = (90 + (thetaY / 2)) - (YAxisAngle2 - 180);
  	   double theta = odo.getTheta();      					    
  	   double correctTheta= (theta + deltaTheta);                
      
  	   odo.setPosition(new double [] {updatedX, updatedY, correctTheta}, new boolean [] {true, true, true});
      
  	   //once we know our current position, travel to (0,0)
  	   Navigation navigation = new Navigation(odo);
  	   navigation.travelTo(0, 0);
  	   navigation.turnTo(0, true);
  	   //Reset odometer now that we are at position (0,0)
  	   odo.setPosition(new double[] {0,0,0}, new boolean[] {true, true, true});
	}
	
	public boolean isNotBlackLine(int lightValue, int currentLight, int lastLight, int lightD) {
		return (lightValue > (currentLight - LightD) && lightValue > (lastLight - LightD) && lightValue > (lightBeforeLastLight - LightD));
	}
	
	//thread sleeps are implemented to minimize the possibility of false negative and positives
	public void sleep() {
		try {Thread.sleep(1000);} catch (InterruptedException e) {};
	} 
	
}
