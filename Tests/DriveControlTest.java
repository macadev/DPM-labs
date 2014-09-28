import junit.framework.Assert;
import junit.framework.TestCase;
import lejos.nxt.Motor;

public class DriveControlTest extends TestCase {
    DriveControl  driveControl = new DriveControl(new MockOdometer(), Motor.A, Motor.B, 1,1);
    public void testTravelTo() throws Exception {
        Assert.assertEquals(true,true);
    }

    public void testTurnTo() throws Exception {

    }

    public void testPolarDisplacement() throws Exception {
        double [] currentPosition;
        double [] destination;
        
        currentPosition = new double[] {0, 0, 90};
        destination = new double[] {1, 1};
        Vector vector = DriveControl.vectorDisplacement(currentPosition, destination);
        Assert.assertEquals(Math.sqrt(2), vector.getMagnitude());
        Assert.assertEquals(Math.PI/4.0, vector.getOrientation());

        currentPosition = new double[] {1, 1, 90};
        destination = new double[] {0, 0};
        vector = DriveControl.vectorDisplacement(currentPosition, destination);
        Assert.assertEquals(Math.sqrt(2), vector.getMagnitude());
        Assert.assertEquals(-3*Math.PI/4.0, vector.getOrientation());

        currentPosition = new double[] {0, 1, 90};
        destination = new double[] {1, 0};
        vector = DriveControl.vectorDisplacement(currentPosition, destination);
        Assert.assertEquals(Math.sqrt(2), vector.getMagnitude());
        Assert.assertEquals(-Math.PI/4.0, vector.getOrientation());

        currentPosition = new double[] {1, 0, 90};
        destination = new double[] {0, 1};
        vector = DriveControl.vectorDisplacement(currentPosition, destination);
        Assert.assertEquals(Math.sqrt(2), vector.getMagnitude());
        Assert.assertEquals(3*Math.PI/4.0, vector.getOrientation());

    }

    public void testComputeOptimalRotationAngle() throws Exception {
        Assert.assertEquals(-60.0, DriveControl.computeOptimalRotationAngle(60, 0));
        Assert.assertEquals(60.0, DriveControl.computeOptimalRotationAngle(300, 0));
        Assert.assertEquals(-60.0, DriveControl.computeOptimalRotationAngle(0, 300));
        Assert.assertEquals(60.0, DriveControl.computeOptimalRotationAngle(0, 60));
        Assert.assertEquals(0.0, DriveControl.computeOptimalRotationAngle(180, 180));
        Assert.assertEquals(2.0, DriveControl.computeOptimalRotationAngle(359, 1));
        Assert.assertEquals(180.0, DriveControl.computeOptimalRotationAngle(0, 180));
        Assert.assertEquals(-180.0, DriveControl.computeOptimalRotationAngle(180, 0));
        Assert.assertEquals(70.0, DriveControl.computeOptimalRotationAngle(300, 10));
    }
}