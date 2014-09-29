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
        Assert.assertEquals(-Math.PI/3, DriveControl.computeOptimalRotationAngle(Math.PI/3, 0));
        Assert.assertEquals(Math.PI/3, DriveControl.computeOptimalRotationAngle(5*Math.PI/3, 0));
        Assert.assertEquals(-Math.PI/3, DriveControl.computeOptimalRotationAngle(0, 300*Math.PI/180));
        Assert.assertEquals(Math.PI/3, DriveControl.computeOptimalRotationAngle(0, 60*Math.PI/180));
        Assert.assertEquals(0.0, DriveControl.computeOptimalRotationAngle(Math.PI, Math.PI));
        Assert.assertEquals(2.0*Math.PI/180, DriveControl.computeOptimalRotationAngle(359*Math.PI/180, 1*Math.PI/180));
        Assert.assertEquals(Math.PI, DriveControl.computeOptimalRotationAngle(0, Math.PI));
        Assert.assertEquals(-Math.PI, DriveControl.computeOptimalRotationAngle(Math.PI, 0));
        Assert.assertEquals(7.0*Math.PI/18, DriveControl.computeOptimalRotationAngle(30*Math.PI/18, 10*Math.PI/180));
    }
}