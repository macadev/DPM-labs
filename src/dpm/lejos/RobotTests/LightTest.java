package dpm.lejos.RobotTests;

import lejos.nxt.*;


/**
 * Simple program reading the value of the light sensor in port 1
 *
 * @author David id. 260583602
 */
public class LightTest {
    public static void main(String [] argv){
        ColorSensor lightSensor = new ColorSensor(SensorPort.S1, 0);
        lightSensor.setFloodlight(true);

        LCD.drawString(String.valueOf(lightSensor.getLightValue()),0,0);
    }
}

