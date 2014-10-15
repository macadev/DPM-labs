

import java.util.ArrayList;
import java.util.Arrays;

import lejos.nxt.LCD;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.comm.RConsole;

/**
 *
 * @author Daniel Macario
 * @version 1.0
 */
public class DeterministicLocalization {

    private Tile[][] plane;
    private UltrasonicSensor us;
    private NXTRegulatedMotor lm;
    private NXTRegulatedMotor rm;
    private double WHEEL_RADIUS;
    private double WHEEL_DISTANCE;
    private static final int FORWARD_SPEED = 300;
    private static final int ROTATE_SPEED = 150;
    private Direction startingDir;
    private Direction endingDir;

    public DeterministicLocalization(UltrasonicSensor us, NXTRegulatedMotor leftMotor, NXTRegulatedMotor rightMotor, double wheel_distance, double wheel_radius) {
        this.plane = createPlane();
        this.us = us;
        this.lm = leftMotor;
        this.rm = rightMotor;
        this.WHEEL_DISTANCE=wheel_distance;
        this.WHEEL_RADIUS=wheel_radius;
    }

    /**
     * Determines the initial location and orientation of the robot
     * based on data obtained from its surroundings.
     *
     * The robot is rotated
     * and moved forward depending on the distances recorded by the US.
     *
     * Then It the trace of movements it performs are simulated on all valid
     * starting positions of the grid, which allows us to cross out all positions
     * until only one remains.
     *
     * At this point we have found the starting position of
     * the robot.
     *
     * After, we compute the ending position based on trace of motions,
     * and finally the robot is driven
     * to the north-east corner and oriented north.
     */
    public void deterministicPositioning() {

        int DISTANCE_THRESHOLD = 30;

        ArrayList<Motion> motionTrace = new ArrayList<Motion>();
        lm.setAcceleration(500);
        rm.setAcceleration(500);
        while(countPossibilities(this.plane) > 1) {

        	sleep(1000);
            int distanceToWall = getFilteredData();

            if (distanceToWall < DISTANCE_THRESHOLD) {
                simulateOnAllTiles(Obstacle.OBSTACLE, motionTrace, plane);
                rotate90CounterClock();
                motionTrace.add(Motion.ROTATE);
            } else {
                simulateOnAllTiles(Obstacle.CLEAR, motionTrace, plane);
                moveForward();
                motionTrace.add(Motion.FORWARD);
            }
        }
        
        Coordinate startingPosition = findStartingPosition();
        Coordinate endingPosition = findEndingPosition(motionTrace, startingPosition);

        printInitialConditions(startingPosition);

        moveToPlaneCorner(endingPosition);

        printGoodbye(motionTrace);
    }

    /**
     * Prints the number of moves performed once we have
     * reached the end of the entire motion
     * @param motionTrace
     */
    private void printGoodbye(ArrayList<Motion> motionTrace){
        LCD.drawString("Completed orienteering", 0,0);
        LCD.drawString("Number of moves:", 0,1);
        LCD.drawString(String.valueOf(motionTrace.size()),0,2);

    }

    /**
     * print the initial conditions to the LCD display
     * @param startingPosition
     */
    private void printInitialConditions(Coordinate startingPosition){
        LCD.clear();

        LCD.drawString("Figured out \ninitial position", 0,3);
        LCD.drawString("X: "+ String.valueOf(startingPosition.getX()), 0,5);
        LCD.drawString("Y: "+ String.valueOf(startingPosition.getY()), 0,6);

    }

    /**
     * finds the initial position and orientation of the robot
     * based on a random series of forward and rotation moves
     *
     * the algorithm records the moves made and compares the possible
     * initial position that would allow such moves to be possible
     *
     */
    public void stochasticPositioning() {

        ArrayList<Motion> motionTrace = new ArrayList<Motion>();
        lm.setAcceleration(500);
        rm.setAcceleration(500);
        while(countPossibilities(this.plane) > 1) {
        	sleep(1000);
            int distanceToWall = getFilteredData();

            if (distanceToWall < 24) {
                simulateOnAllTiles(Obstacle.OBSTACLE, motionTrace, plane);
                rotate90CounterClock();
                motionTrace.add(Motion.ROTATE);
            } else {
            	boolean shouldRotate = getRandomBoolean();
            	
            	if (shouldRotate) {
            		simulateOnAllTiles(Obstacle.CLEAR, motionTrace, plane);
                    rotate90CounterClock();
                    motionTrace.add(Motion.ROTATE);
            	} else {
            		simulateOnAllTiles(Obstacle.CLEAR, motionTrace, plane);
                    moveForward();
                    motionTrace.add(Motion.FORWARD);
            	}
            }
        }
        
        Coordinate startingPosition = findStartingPosition();

        Coordinate endingPosition = findEndingPosition(motionTrace, startingPosition);
        printInitialConditions(startingPosition);
        moveToPlaneCorner(endingPosition);

        printGoodbye(motionTrace);
    }

    /**
     * Returns a random boolean based on Java's Math.random()
     * function. Used for stochastic positioning
     * * @return a boolean variable
     */
    public boolean getRandomBoolean() {
        return Math.random() < 0.5;
        //I tried another approaches here, still the same result
    }

    /**
     * move the robot to a corner facing north
     * @param endingPosition the tile we want to move to
     */
    public void moveToPlaneCorner(Coordinate endingPosition) {
		Direction currentDirection;
    	int y = endingPosition.getY();
		int x = endingPosition.getX();
		rotateNorth(null);
		currentDirection = Direction.NORTH;
		while (true) {
			sleep(1000);
			
			if (currentDirection != Direction.NORTH) {
				rotateNorth(currentDirection);
				currentDirection = Direction.NORTH;
			} 
			
			int distanceToWall = getFilteredData();
			
			if (y == 0 && x == 3) {
				rotateNorth(currentDirection);
				break;
			}
			
			if (y > 0 && distanceToWall > 24 && currentDirection == Direction.NORTH) {
				moveForward();
				y--;
			} else if (x == 1 && y != 0) {
				moveForward();
				y--;
			} else if (y > 0 && distanceToWall < 24 && x > 1) {
				rotate90CounterClock();
				moveForward();
				currentDirection = Direction.WEST;
				x--;
			} else if (y > 0 && distanceToWall < 24 && x < 1) {
				rotate90ClockWise();
				moveForward();
				currentDirection = Direction.EAST;
				x++;
			} else if (y == 0) {
				rotate90ClockWise();
				currentDirection = Direction.EAST;
				while (getFilteredData() > 24) {
					moveForward();
					x++;
				}
			}
		}
	}

    /**
     * Calculates the starting location and orientation of the robot
     * once it has has finished eliminating impossible
     * starting points on the grid
     * @return Coordinates where the robot started
     */
    public Coordinate findStartingPosition() {
    	
    	Direction[] directions = { Direction.NORTH, 
    							   Direction.SOUTH,
    							   Direction.EAST,
    							   Direction.WEST 
              					 };
    	
    	Coordinate coord = new Coordinate(0,0);
    	for(Direction dir : directions) {
    		for(int i = 0; i < 4; i++) {
    			for(int j = 0; j < 4; j++) {
    				if (plane[i][j].isPossible(dir)) {
    					coord = new Coordinate(j,i);
    					this.startingDir = dir;
    					break;
    				}
    			}
    		}
    	}
    	return coord;
	}

    /**
     * using the virtual implementation of the robot and the
     * stack of recorded moves, we find out where the orienteering
     * algorithm has lead us
     *
     * @param motionTrace the stack of recorded moves
     * @param startingPosition the initial position
     * @return the current position
     */
	public Coordinate findEndingPosition(ArrayList<Motion> motionTrace, Coordinate startingPosition) {
		VirtualRobot vr = new VirtualRobot(startingPosition.getX(), startingPosition.getY(), this.startingDir);
		if (!motionTrace.isEmpty()) {
            for (Motion motion : motionTrace ) {
                if (motion == Motion.FORWARD ) {
                    vr.moveForward();
                } else {
                    vr.rotate();
                }
            }
        }
		this.endingDir = vr.getDir();
        return new Coordinate(vr.getX(), vr.getY());
    }

    /**
     * position the robot facing north
     * @param dir the current heading
     */
	public void rotateNorth(Direction dir) {
		if (dir == null) {
			if (this.endingDir == Direction.SOUTH) {
	    		rotate90CounterClock();
	    		rotate90CounterClock();
	    	} else if (this.endingDir == Direction.EAST) {
	    		rotate90CounterClock();
	    	} else if (this.endingDir == Direction.WEST) {
	    		rotate90ClockWise();
	    	}
		} else {
			if (dir == Direction.SOUTH) {
	    		rotate90CounterClock();
	    		rotate90CounterClock();
	    	} else if (dir == Direction.EAST) {
	    		rotate90CounterClock();
	    	} else if (dir == Direction.WEST) {
	    		rotate90ClockWise();
	    	}
		}
    }

    /**
     * rotate the physical robot 90 degrees counterclockwise
     */
    public void rotate90CounterClock() {
    	lm.setSpeed(ROTATE_SPEED);
        rm.setSpeed(ROTATE_SPEED);
        lm.rotate(convertAngle(-90), true);
        rm.rotate(convertAngle(90), false);
    }

    /**
     * rotate the physical robot 90 degrees clockwise
     */
    public void rotate90ClockWise() {
    	lm.setSpeed(ROTATE_SPEED);
        rm.setSpeed(ROTATE_SPEED);
        lm.rotate(convertAngle(90), true);
        rm.rotate(convertAngle(-90), false);
    }

    /**
     * move the robot one tile forward
     */
    public void moveForward() {
    	lm.setSpeed(FORWARD_SPEED);
        rm.setSpeed(FORWARD_SPEED);
        lm.rotate(convertDistance(30), true);
        rm.rotate(convertDistance(30), false);
    }

    /**
     * simulate the recorded motion for all
     * starting position possible
     * @param obs is there an obstacle in front of us
     * @param motionTrace the stack of movements applied so far
     * @param plane the playground layout
     */
    public void simulateOnAllTiles(Obstacle obs, ArrayList<Motion> motionTrace, Tile[][] plane) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                for (int k = 0; k < 4; k++) {
                    if (!plane[i][j].isObstacle()) {
                        Direction selectedDir;
                        VirtualRobot vr;
                        if (k == 0) {
                            vr = new VirtualRobot(j, i, Direction.NORTH);
                            selectedDir = Direction.NORTH;
                        } else if (k == 1) {
                            vr = new VirtualRobot(j, i, Direction.EAST);
                            selectedDir = Direction.EAST;
                        } else if (k == 2) {
                            vr = new VirtualRobot(j, i, Direction.WEST);
                            selectedDir = Direction.WEST;
                        } else {
                            vr = new VirtualRobot(j, i, Direction.SOUTH);
                            selectedDir = Direction.SOUTH;
                        }

                        if (plane[i][j].isPossible(vr.getDir())) {
                            if (!motionTrace.isEmpty()) {
                                for (Motion motion : motionTrace ) {
                                    if (motion == Motion.FORWARD ) {
                                        vr.moveForward();
                                    } else {
                                        vr.rotate();
                                    }
                                }
                            }

                            boolean hasObstacle = plane[vr.getY()][vr.getX()].hasObstacle(vr.getDir());
                            if (hasObstacle && obs == Obstacle.CLEAR || !hasObstacle && obs == Obstacle.OBSTACLE) {
                                plane[i][j].setPossibilityToFalse(selectedDir);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Count the remaining possibilities for the starting position
     * @param plane the plane object reference
     * @return the number of possible starting position
     */
    public int countPossibilities(Tile[][] plane) {
        int posCount = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (!plane[i][j].isObstacle()) {
                    if (plane[i][j].isPossible(Direction.NORTH)) posCount++;
                    if (plane[i][j].isPossible(Direction.SOUTH)) posCount++;
                    if (plane[i][j].isPossible(Direction.WEST)) posCount++;
                    if (plane[i][j].isPossible(Direction.EAST)) posCount++;
                }
            }
        }
        return posCount;
    }

    /**
     * Create the Tile array with the walls
     * and place the obstacles on the playground
     * @return The newly created and populated Tile array
     */
    public Tile[][] createPlane() {
        Tile[][] plane = new Tile[4][4];

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                plane[i][j] = new Tile();
            }
        }

        for (int i = 0; i < 4; i++) {
            plane[0][i].setObstacle(Direction.NORTH, true);
            plane[3][i].setObstacle(Direction.SOUTH, true);
            plane[i][0].setObstacle(Direction.WEST, true);
            plane[i][3].setObstacle(Direction.EAST, true);
        }
        fillRemainingPositions(plane);

        countPossibilities(plane);

        return plane;
    }

    /**
     * setup for initial playground layout
     * @param plane the reference to the Tile array
     */
    public void fillRemainingPositions(Tile[][] plane) {
        plane[0][1].setObstacle(Direction.WEST, true);
        plane[0][2].setObstacle(Direction.SOUTH, true);
        plane[0][3].setObstacle(Direction.SOUTH, true);
        plane[1][0].setObstacle(Direction.NORTH, true);
        plane[1][1].setObstacle(Direction.EAST, true);
        plane[2][2].setObstacle(Direction.NORTH, true);
        plane[2][1].setObstacle(Direction.SOUTH, true);
        plane[2][3].setObstacle(Direction.NORTH, true);
        plane[3][0].setObstacle(Direction.EAST, true);
        plane[3][2].setObstacle(Direction.WEST, true);
        plane[0][0].setObstacle(true);
        plane[1][2].setObstacle(true);
        plane[1][3].setObstacle(true);
        plane[3][1].setObstacle(true);
        //close possibilities of tiles that are obstacles
        plane[0][0].closeAllPossibilities();
        plane[1][2].closeAllPossibilities();
        plane[1][3].closeAllPossibilities();
        plane[3][1].closeAllPossibilities();
    }

    /**
     * Conversion from desired travel distance to motor rotation angle (tacho count)
     * @param distance distance to travel
     * @return number of degrees the motor should travel to match the desired distance
     */
    private int convertDistance(double distance) {
        return (int) ((180.0 * distance) / (Math.PI * this.WHEEL_RADIUS));
    }

    /**
     * Translates a desired rotation of the robot around its center to a
     * number of degrees each wheel should turn
     *
     * @param angle the angle the robot should rotate
     * @return the angle a motor should travel for the robot to rotate
     */
    private int convertAngle(double angle) {
        return convertDistance(Math.PI * this.WHEEL_DISTANCE * angle / 360.0);
    }

    /**
     * take five readings with the ultrasonic sensor
     * and return the median value
     * @return the filtered distance read with the us sensor
     */
    private int getFilteredData() {
		int distance;
		int[] dist = new int[5];
		for (int i = 0; i < 5; i++) {
			us.ping();

			// wait for ping to complete
			sleep(50);
			// there will be a delay
			dist[i] = us.getDistance();

		}

        Arrays.sort(dist);
		distance = dist[2];
				
		return distance;
	}

    /**
     * Sleep thread 1 second
     */
	public void sleep(int delay) {
		try { Thread.sleep(delay); } catch (InterruptedException e) {e.printStackTrace();}
	}
}
