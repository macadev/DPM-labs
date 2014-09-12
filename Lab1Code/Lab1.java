import lejos.nxt.*;
import lejos.nxt.comm.NXTConnection;
import lejos.nxt.comm.RConsole;
import lejos.nxt.comm.USB;
import lejos.util.LogColumn;
import lejos.util.NXTDataLogger;

import java.io.IOException;


public class Lab1 {
	
	private static final SensorPort usPort = SensorPort.S1;
	//private static final SensorPort lightPort = SensorPort.S2;
	
	private static final int desiredCenter = 20, bandWidth = 3;
	private static final int motorLow = 100, motorHigh = 400;
	
	
	public static void main(String [] args) {
		/*
		 * Wait for startup button press
		 * Button.ID_LEFT = BangBang Type
		 * Button.ID_RIGHT = P Type
		 */
        RConsole.openUSB(5000);
        RConsole.println("Connected");

        NXTDataLogger dlog = new NXTDataLogger();
        NXTConnection conn = USB.waitForConnection(5000, NXTConnection.PACKET);

        try {
            dlog.startRealtimeLog(conn);
        } catch (IOException e) {
            // Do nothing. This is hideously bad.
        }

        dlog.setColumns(new LogColumn[] {
                new LogColumn("speed left", LogColumn.DT_INTEGER),
                new LogColumn("speed right", LogColumn.DT_INTEGER),
                new LogColumn("Distance", LogColumn.DT_INTEGER,3),// use different range axis (3)
        });

        int option = 0;
		Printer.printMainMenu();
		while (option == 0)
			option = Button.waitForAnyPress();
		
		// Setup controller objects
		BangBangController bangbang = new BangBangController(desiredCenter, bandWidth, motorLow, motorHigh, dlog);
		PController p = new PController(desiredCenter, bandWidth);
		
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
