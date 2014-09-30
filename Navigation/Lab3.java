import lejos.nxt.*;

/**
 * Lab3 main executable
 *
 * @author David id. 260583602
 */
public class Lab3 {

    public static void main (String [] argv){
        Driver driver;
        int buttonChoice;

        do {
            // clear the display
            LCD.clear();

            // ask the user whether the motors should drive in a square or float
            LCD.drawString("< Left | Right >", 0, 0);
            LCD.drawString("       |        ", 0, 1);
            LCD.drawString(" Part  |  Part  ", 0, 2);
            LCD.drawString("   A   |    B   ", 0, 3);
            LCD.drawString("       |        ", 0, 4);

            buttonChoice = Button.waitForAnyPress();
        } while (buttonChoice != Button.ID_LEFT
                && buttonChoice != Button.ID_RIGHT);

        if (buttonChoice == Button.ID_LEFT) {
            driver = new Driver(Driver.AvailablePath.PART_A);
            driver.start();

        } else {
            driver = new Driver(Driver.AvailablePath.PART_B);
            driver.start();

        }

        while (Button.waitForAnyPress()!=Button.ID_ESCAPE){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.exit(0);
    }
}
