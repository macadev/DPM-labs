/**
 * Created by David on 14-09-28.
 * id. 260583602
 */
public class Vector {
    private double magnitude;
    private double orientation;

    public Vector (){}
    public Vector (double magnitude, double orientation){
        this.magnitude = magnitude;
        this.orientation = orientation;
    }

    public double getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(double magnitude) {
        this.magnitude = magnitude;
    }

    public double getOrientation() {
        return orientation;
    }

    public void setOrientation(double orientation) {
        this.orientation = orientation;
    }

    public double [] toCartesian(){
        return new double [] {magnitude*Math.sin(orientation), magnitude*Math.cos(orientation)};
    }
}
