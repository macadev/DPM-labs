import dpm.lejos.Lab3Code.ConversionUtilities;
import junit.framework.TestCase;

/**
 * Unit tests for the conversions utilities
 */
public class ConversionUtilitiesTest extends TestCase {

    public void testConvertDistanceToMotorRotation() throws Exception {
        assertEquals(204, ConversionUtilities.convertDistanceToMotorRotation(2.8, 10));
        assertEquals(-204, ConversionUtilities.convertDistanceToMotorRotation(2.8, -10));
}

    public void testConvertAngleToMotorRotation() throws Exception {

        assertEquals(321, ConversionUtilities.convertAngleToMotorRotation(2.8, 10, Math.PI));
        assertEquals(-321, ConversionUtilities.convertAngleToMotorRotation(2.8, 10, -Math.PI));
    }
}