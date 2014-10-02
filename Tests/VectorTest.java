import dpm.lejos.Lab3Code.Vector;
import junit.framework.TestCase;

/**
 * Unit tests for the Vector type
 */
public class VectorTest extends TestCase {

    public void testGetMagnitude() throws Exception {
        Vector vector = new Vector(10, Math.PI);

        assertEquals(10.0, vector.getMagnitude(), 0.001);
    }

    public void testGetOrientation() throws Exception {
        Vector vector = new Vector(10, Math.PI);

        assertEquals(Math.PI, vector.getOrientation(), 0.001);
    }

    public void testSetMagnitude() throws Exception {
        Vector vector = new Vector();
        vector.setMagnitude(10);
        assertEquals(10.0, vector.getMagnitude(), 0.001);
    }

    public void testSetOrientation() throws Exception {
        Vector vector = new Vector();
        vector.setOrientation(Math.PI);
        assertEquals(Math.PI, vector.getOrientation(), 0.001);
    }

    public void testToCartesian() throws Exception {
        Vector vector = new Vector(Math.sqrt(2),Math.PI/4);
        double[] coords = vector.toCartesian();

        assertEquals(coords[0], 1.0, 0.001);
        assertEquals(coords[1], 1.0, 0.001);
    }
}