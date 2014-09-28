/**
 * Created by David on 14-09-22.
 * id. 260583602
 */
public class FloorTest {
    private static final int THETA_THRESHOLD = 4;

    public static void main (String [] argv){
        System.out.println("ceil of 0.2: "+String.valueOf(-2.4%1));
        System.out.println("ceil of 0.5: "+String.valueOf(0.5%1));
        System.out.println("ceil of 0.8: "+String.valueOf(0.8%1));
        System.out.println("ceil of 1.2: "+String.valueOf(1.2%1));
        System.out.println("ceil of 1.7: "+String.valueOf(1.7%1));
        double[] position = {14.8,52.4,-183};
        double orientation = position[2];

        if (Math.abs(orientation) < THETA_THRESHOLD) {
            //rotated 0 degrees, travelling in y
            double frac = position[1]/15.0;
            double disIncrement = Math.abs(frac%1)<0.5 ?  Math.floor(frac) : Math.ceil(frac);
            System.out.println(disIncrement*15);
        } else if (Math.abs(orientation + 90) < THETA_THRESHOLD) {
            //rotated 90 degrees, travelling in x
            double frac = position[1]/15.0;
            double disIncrement = Math.abs(frac%1)<0.5 ?  Math.floor(frac) : Math.ceil(frac);
            System.out.println(disIncrement*15);
        } else if (Math.abs(orientation + 180) < THETA_THRESHOLD) {
            //rotated 180 degrees, travelling in y
            double frac = position[1]/15.0;
            double disIncrement = Math.abs(frac%1)<0.5 ?  Math.floor(frac) : Math.ceil(frac);
            System.out.println(disIncrement*15);
        } else if (Math.abs(orientation + 270) < THETA_THRESHOLD) {
            //rotated 270 degrees, travelling in x
            double frac = position[0]/15.0;
            double disIncrement = Math.abs(frac%1)<0.5 ?  Math.floor(frac) : Math.ceil(frac);
            System.out.println(disIncrement*15);
        }
    }
}
