import junit.framework.Assert;
import junit.framework.TestCase;

import java.util.Map;

/**
 * Unit tests for the Vector type
 */
public class VectorTest extends TestCase {

    public void testGetMagnitude() throws Exception {
        Vector vector = new Vector(10, Math.PI);

        Assert.assertEquals(10.0, vector.getMagnitude(), 0.001);
    }

    public void testGetOrientation() throws Exception {
        Vector vector = new Vector(10, Math.PI);

        Assert.assertEquals(Math.PI, vector.getOrientation(), 0.001);
    }

    public void testSetMagnitude() throws Exception {
        Vector vector = new Vector();
        vector.setMagnitude(10);
        Assert.assertEquals(10.0, vector.getMagnitude(), 0.001);
    }

    public void testSetOrientation() throws Exception {
        Vector vector = new Vector();
        vector.setOrientation(Math.PI);
        Assert.assertEquals(Math.PI, vector.getOrientation(), 0.001);
    }

    public void testToCartesian() throws Exception {
        Vector vector = new Vector(Math.sqrt(2),Math.PI/4);
        double[] coords = vector.toCartesian();

        Assert.assertEquals(coords[0], 1.0, 0.001);
        Assert.assertEquals(coords[1], 1.0, 0.001);
    }
}