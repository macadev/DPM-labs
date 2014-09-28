import lejos.nxt.*;


/**
 * Created by David on 14-09-22.
 * id. 260583602
 */
public class LightTest {
    public static void main(String [] argv){
        ColorSensor lightSensor = new ColorSensor(SensorPort.S1, 0);
        lightSensor.setFloodlight(true);

        LCD.drawString(String.valueOf(lightSensor.getLightValue()),0,0);
    }
}

