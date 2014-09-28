/**
 * Created by David on 14-09-26.
 * id. 260583602
 */
public class MockOdometer extends Odometer {
    /**
     * Mock odometer overrides the get theta method fir unit tests, there are no encoders
     * @return angle of 0
     */
    @Override
    public double getTheta(){
        return 0.0;
    }
}
