import lejos.nxt.*;
import lejos.nxt.comm.RConsole;


public class Lab1 {
	
	private static final SensorPort usPort = SensorPort.S1;
	//private static final SensorPort lightPort = SensorPort.S2;
	
	private static final int desiredCenter = 30, bandWidth = 3;
	private static final int motorHigh = 400, motorStraigth = 200;
	
	
	public static void main(String [] args) {
		/*
		 * Wait for startup button press
		 * Button.ID_LEFT = BangBang Type
		 * Button.ID_RIGHT = P Type
		 */
        RConsole.openUSB(3000);
        RConsole.println("Connected");
		int option = 0;
		Printer.printMainMenu();
		while (option == 0)
			option = Button.waitForAnyPress();
		
		// Setup controller objects
		BangBangController bangbang = new BangBangController(desiredCenter, bandWidth, motorHigh, motorStraigth);
		PController p = new PController(desiredCenter, bandWidth, motorStraigth, motorHigh);
		
		// Setup ultrasonic sensor
		UltrasonicSensor usSensor = new UltrasonicSensor(usPort);
		
		// Setup Printer
		Printer printer = null;
		
		// Setup Ultrasonic Poller
		UltrasonicPoller usPoller = null;
		
		switch(option) {
		case Button.ID_LEFT:
			usPoller = new UltrasonicPoller(usSensor, bangbang);
			printer = new Printer(option, bangbang);
			break;
		case Button.ID_RIGHT:
			usPoller = new UltrasonicPoller(usSensor, p);
			printer = new Printer(option, p);
			break;
		default:
			System.out.println("Error - invalid button");
			System.exit(-1);
			break;
		}
		
		usPoller.start();
		printer.start();
		
		//Wait for another button press to exit
		Button.waitForAnyPress();
		System.exit(0);
		
	}
}
