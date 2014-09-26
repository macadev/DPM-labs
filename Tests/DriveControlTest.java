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

    public void testIsNavigating() throws Exception {

    }

    public void testComputeOptimalRotationAngle() throws Exception {
        Assert.assertEquals(-60.0, driveControl.computeOptimalRotationAngle(60, 0));
        Assert.assertEquals(60.0, driveControl.computeOptimalRotationAngle(300, 0));
        Assert.assertEquals(-60.0, driveControl.computeOptimalRotationAngle(0, 300));
        Assert.assertEquals(60.0, driveControl.computeOptimalRotationAngle(0, 60));
        Assert.assertEquals(0.0, driveControl.computeOptimalRotationAngle(180, 180));
        Assert.assertEquals(2.0, driveControl.computeOptimalRotationAngle(359, 1));
        Assert.assertEquals(180.0, driveControl.computeOptimalRotationAngle(0, 180));
        Assert.assertEquals(-180.0, driveControl.computeOptimalRotationAngle(180, 0));
        Assert.assertEquals(70.0, driveControl.computeOptimalRotationAngle(300, 10));
    }
}