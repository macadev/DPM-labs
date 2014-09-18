import lejos.nxt.NXTRegulatedMotor;

/**
 * Created by David on 2014-09-18.
 *
 */
public class GyroMotor extends Thread {
    private NXTRegulatedMotor motor;
    private GyroPoller poller;
    public GyroMotor (NXTRegulatedMotor motor, GyroPoller poller){
        this.motor = motor;
        this.poller=poller;
    }

    @Override
    public void run() {
        while(true) {
            float angle = poller.getAngle();
            motor.rotateTo((int) angle);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
