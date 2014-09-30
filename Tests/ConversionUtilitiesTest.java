import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Unit tests for the conversions utilities
 */
public class ConversionUtilitiesTest extends TestCase {

    public void testConvertDistanceToMotorRotation() throws Exception {
        Assert.assertEquals(204, ConversionUtilities.convertDistanceToMotorRotation(2.8, 10));
        Assert.assertEquals(-204, ConversionUtilities.convertDistanceToMotorRotation(2.8, -10));
}

    public void testConvertAngleToMotorRotation() throws Exception {

        Assert.assertEquals(321, ConversionUtilities.convertAngleToMotorRotation(2.8, 10, Math.PI));
        Assert.assertEquals(-321, ConversionUtilities.convertAngleToMotorRotation(2.8, 10, -Math.PI));
    }
}