package dpm.lejos.test;

import dpm.lejos.Lab3Code.Navigation;
import dpm.lejos.Lab3Code.Vector;
import junit.framework.TestCase;
import lejos.nxt.Motor;

/**
 * Unit tests for the logic functions in Navigation class
 */
public class NavigationTest extends TestCase {
    Navigation navigation = new Navigation(new MockOdometer(), new MockUsSensor(),Motor.A, Motor.B, 1,1);

    public void testPolarDisplacement() throws Exception {
        double [] currentPosition;
        double [] destination;



        currentPosition = new double[] {0, 0, 90};
        destination = new double[] {0, 60};
        Vector vector = Navigation.vectorDisplacement(currentPosition, destination);
        assertEquals(60.0, vector.getMagnitude());
        assertEquals(Math.PI/2, vector.getOrientation());

        currentPosition = new double[] {0, 60, 90};
        destination = new double[] {60, 0};
        vector = Navigation.vectorDisplacement(currentPosition, destination);
        assertEquals(Math.sqrt((60*60)+(60*60)), vector.getMagnitude());
        assertEquals(-Math.PI/4, vector.getOrientation());

        currentPosition = new double[] {1, 1, 90};
        destination = new double[] {0, 0};
        vector = Navigation.vectorDisplacement(currentPosition, destination);
        assertEquals(Math.sqrt(2), vector.getMagnitude());
        assertEquals(-3*Math.PI/4.0, vector.getOrientation());

        currentPosition = new double[] {0, 1, 90};
        destination = new double[] {1, 0};
        vector = Navigation.vectorDisplacement(currentPosition, destination);
        assertEquals(Math.sqrt(2), vector.getMagnitude());
        assertEquals(-Math.PI/4.0, vector.getOrientation());

        currentPosition = new double[] {1, 0, 90};
        destination = new double[] {0, 1};
        vector = Navigation.vectorDisplacement(currentPosition, destination);
        assertEquals(Math.sqrt(2), vector.getMagnitude());
        assertEquals(3*Math.PI/4.0, vector.getOrientation());

    }

    public void testComputeOptimalRotationAngle() throws Exception {
        assertEquals(-Math.PI/3, Navigation.computeOptimalRotationAngle(Math.PI/3, 0),0.001);
        assertEquals(Math.PI/3, Navigation.computeOptimalRotationAngle(5*Math.PI/3, 0), 0.001);
        assertEquals(-Math.PI/3, Navigation.computeOptimalRotationAngle(0, 300*Math.PI/180), 0.001);
        assertEquals(Math.PI/3, Navigation.computeOptimalRotationAngle(0, 60*Math.PI/180), 0.001);
        assertEquals(0.0, Navigation.computeOptimalRotationAngle(Math.PI, Math.PI), 0.001);
        assertEquals(2.0*Math.PI/180, Navigation.computeOptimalRotationAngle(359*Math.PI/180, 1*Math.PI/180), 0.001);
        assertEquals(Math.PI, Navigation.computeOptimalRotationAngle(0, Math.PI), 0.001);
        assertEquals(-Math.PI, Navigation.computeOptimalRotationAngle(Math.PI, 0), 0.001);
        assertEquals(7.0*Math.PI/18, Navigation.computeOptimalRotationAngle(30*Math.PI/18, 10*Math.PI/180), 0.001);
    }
}