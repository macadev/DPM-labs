import lejos.nxt.I2CPort;
import lejos.nxt.UltrasonicSensor;

/**
 * @author David id. 260583602
 */
public class MockUsSensor extends UltrasonicSensor {

    public MockUsSensor() {
        super(new I2CPort() {
            @Override
            public void i2cEnable(int i) {

            }

            @Override
            public void i2cDisable() {

            }

            @Override
            public int i2cStatus() {
                return 0;
            }

            @Override
            public int i2cTransaction(int i, byte[] bytes, int i2, int i3, byte[] bytes2, int i4, int i5) {
                return 0;
            }

            @Override
            public int getMode() {
                return 0;
            }

            @Override
            public int getType() {
                return 0;
            }

            @Override
            public void setMode(int i) {

            }

            @Override
            public void setType(int i) {

            }

            @Override
            public void setTypeAndMode(int i, int i2) {

            }
        });
    }

    @Override
    public int getDistance(){
        return 255;
    }
}
