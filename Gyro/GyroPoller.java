import lejos.nxt.addon.GyroDirectionFinder;
import lejos.nxt.comm.RConsole;

/**
 * Created by David on 2014-09-18.
 *
 *
 */
public class GyroPoller extends Thread {
    private final GyroDirectionFinder gyro;

    private float angle;
    public GyroPoller(GyroDirectionFinder gyro){
        this.gyro=gyro;
        angle=0;
    }

    @Override
    public void run(){
        while (true){
            angle=gyro.getDegrees();
            RConsole.println(String.valueOf(angle));
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public float getAngle() {
        return angle;
    }
}
