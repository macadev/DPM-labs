package dpm.lejos.Lab4Code;

import lejos.nxt.LightSensor;

public class LightLocalizer {
    private Odometer odometer;
    private TwoWheeledRobot robot;
    private LightSensor ls;

    private static final int ROTATIONAL_SPEED = 20;
    private static final double ERROR = 1.0; // An error
    private int lineCounter = 0; // A lineCounter that keeps track of every time a black line is sensed
    // The angles each time the robot senses a black line
    private double angleA, angleB, angleC, angleD;
    // The distance the sensor distance is from the center of the robot
    public static double SENSOR_DISTANCE = 5.0;


    public LightLocalizer(Odometer odometer, LightSensor ls) {
        this.odometer = odometer;
        this.robot = odometer.getTwoWheeledRobot();
        this.ls = ls;

        // turn on the light
        ls.setFloodlight(true);
    }

    public void doLocalization() {
        // drive to location listed in tutorial
        // start rotating and clock all 4 gridlines
        // do trig to compute (0,0) and 0 degrees
        // when done travel to (0,0) and turn to 0 degrees


        // Passing through the current odometer to the navigation
        // Keeping track of time to prevent the robot from updating the line lineCounter too much when it senses a line

        // Determining the start angle of the robot
        double startAngle = odometer.getTheta();

        // This for loop makes sure that the angle rotates a bit so that a starting angle can be
        // determined since the robot is orientated at 0 degrees at this point in the demo.

        for (int i = 0; i < 250; i++) {

            robot.setRotationSpeed(-ROTATIONAL_SPEED);

        }
        // Tracking time of the robot
        double currentTime = System.nanoTime();
        double lastTime = currentTime - 200 * Math.pow(10, 6); // Multiplying the time by 10^6 because time is in nano seconds

        // While loop makes sure that the robot keeps rotating until it reaches its original starting angle position
        // which is known from originally rotating it for a certain time. (startAngle variable stores this angle)
        // It ends up rotating in a circle and it is able to track all 4 black lines.

        while (Math.abs(startAngle - odometer.getTheta()) > ERROR) {
            // Keeps track of time
            // Getting the light sensor readings
            double lightValue = ls.getNormalizedLightValue();

            // This is the condition for sensing a black line. Black lines occur when it is approximately
            // less than 500 so a threshold value used was a reading of 420. Also, the time difference between it
            // senses a black line to the next must be larger 2 seconds. This will make sure that the robot
            // does not increment the black line lineCounter by more than 1 every time it senses a black line because
            // of the frequency of the light sensor

            currentTime = System.nanoTime();
            if ((lightValue < 420) && (currentTime - lastTime > (200 * Math.pow(10, 6)))) {


                lastTime = System.nanoTime();
                lineCounter++;

                // The angle is recorded each time the robot detects a black line.
                // Counter corresponds to the nth black line it senses. After sensing 4
                // lines, we know that it has completed a circle and returns to its original
                // position when light localizing started

                if (lineCounter == 1) angleA = odometer.getTheta();
                if (lineCounter == 2) angleB = odometer.getTheta();
                if (lineCounter == 3) angleC = odometer.getTheta();
                if (lineCounter == 4) angleD = odometer.getTheta();

            }
        }
        // Converting the angles so the math is correct when calculating x and y positions
        if (angleA > 180) angleA = angleA - 180;
        if (angleB > 180) angleB = angleB - 180;
        if (angleC > 180) angleC = angleC - 180;
        if (angleD > 180) angleD = angleD - 180;

        // The robot stops after tracking all 4 angles
        robot.setRotationSpeed(0);

        // Calculating the difference in angles of the 1st and 3rd and 2nd and 4th black line
        // to determine the x and y position

        double thetaY = angleC - angleA;
        double thetaX = angleD - angleB;

        // Calcating the robot/s position based on trignometric equations (provided in the tutorial slides)

        double xPosition = -(SENSOR_DISTANCE) * (Math.cos(thetaY / 2));
        double yPosition = -(SENSOR_DISTANCE) * (Math.cos(thetaX / 2));

        // Calculating the correction angle of the robot
        double deltaTheta = 180 + thetaY / 2 - angleD;

        // Setting the odometer angle after it does the light localization with the correction
        // angle (deltaTheta) taken into consideration
        odometer.setTheta(odometer.getTheta() + deltaTheta);

        // Once localized, travel to (0,0)
        Navigation navigation = new Navigation(odometer);
        navigation.travelTo(0, 0);

        // The following lines describe the method of physically adjusting the robot so that it points
        // directly north (0 degrees) with the robot perfectly aligned along the black lines
        // After driving to the origin, the robot will turn one way to correct for the slight
        // deviation in the angle of the robot. It will turn one way for 3 seconds and if it doesn't
        // sense a line, it will turn the other way to

        int coeff = 1;
        double timeStart = System.nanoTime() * Math.pow(10, 9);
        while (ls.getNormalizedLightValue() > 450) {
            robot.setRotationSpeed(-5 * coeff);
            if (System.nanoTime() * Math.pow(10, 9) - timeStart > 3) coeff = -2;
        }

        robot.stop();

        // Setting the position and angle of the robot to all 0's since it is at the origin orientated
        // towards north
        odometer.setPosition(
                new double[]{0.0, 0.0, 0.0},
                new boolean[]{true, true, true});
    }

}
