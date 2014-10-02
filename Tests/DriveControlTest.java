import dpm.lejos.Lab3Code.DriveControl;
import dpm.lejos.Lab3Code.Vector;
import junit.framework.TestCase;
import lejos.nxt.Motor;

/**
 * Unit tests for the logic functions in DriveControl class
 */
public class DriveControlTest extends TestCase {
    DriveControl driveControl = new DriveControl(new MockOdometer(), new MockUsSensor(),Motor.A, Motor.B, 1,1);

    public void testPolarDisplacement() throws Exception {
        double [] currentPosition;
        double [] destination;



        currentPosition = new double[] {0, 0, 90};
        destination = new double[] {0, 60};
        Vector vector = DriveControl.vectorDisplacement(currentPosition, destination);
        assertEquals(60.0, vector.getMagnitude());
        assertEquals(Math.PI/2, vector.getOrientation());

        currentPosition = new double[] {0, 60, 90};
        destination = new double[] {60, 0};
        vector = DriveControl.vectorDisplacement(currentPosition, destination);
        assertEquals(Math.sqrt((60*60)+(60*60)), vector.getMagnitude());
        assertEquals(-Math.PI/4, vector.getOrientation());

        currentPosition = new double[] {1, 1, 90};
        destination = new double[] {0, 0};
        vector = DriveControl.vectorDisplacement(currentPosition, destination);
        assertEquals(Math.sqrt(2), vector.getMagnitude());
        assertEquals(-3*Math.PI/4.0, vector.getOrientation());

        currentPosition = new double[] {0, 1, 90};
        destination = new double[] {1, 0};
        vector = DriveControl.vectorDisplacement(currentPosition, destination);
        assertEquals(Math.sqrt(2), vector.getMagnitude());
        assertEquals(-Math.PI/4.0, vector.getOrientation());

        currentPosition = new double[] {1, 0, 90};
        destination = new double[] {0, 1};
        vector = DriveControl.vectorDisplacement(currentPosition, destination);
        assertEquals(Math.sqrt(2), vector.getMagnitude());
        assertEquals(3*Math.PI/4.0, vector.getOrientation());

    }

    public void testComputeOptimalRotationAngle() throws Exception {
        assertEquals(-Math.PI/3, DriveControl.computeOptimalRotationAngle(Math.PI/3, 0),0.001);
        assertEquals(Math.PI/3, DriveControl.computeOptimalRotationAngle(5*Math.PI/3, 0), 0.001);
        assertEquals(-Math.PI/3, DriveControl.computeOptimalRotationAngle(0, 300*Math.PI/180), 0.001);
        assertEquals(Math.PI/3, DriveControl.computeOptimalRotationAngle(0, 60*Math.PI/180), 0.001);
        assertEquals(0.0, DriveControl.computeOptimalRotationAngle(Math.PI, Math.PI), 0.001);
        assertEquals(2.0*Math.PI/180, DriveControl.computeOptimalRotationAngle(359*Math.PI/180, 1*Math.PI/180), 0.001);
        assertEquals(Math.PI, DriveControl.computeOptimalRotationAngle(0, Math.PI), 0.001);
        assertEquals(-Math.PI, DriveControl.computeOptimalRotationAngle(Math.PI, 0), 0.001);
        assertEquals(7.0*Math.PI/18, DriveControl.computeOptimalRotationAngle(30*Math.PI/18, 10*Math.PI/180), 0.001);
    }
}