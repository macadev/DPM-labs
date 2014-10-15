

import java.util.ArrayList;

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
    private static double WHEEL_RADIUS = 2.1;
    private static double WHEEL_DISTANCE = 15;
    private static final int FORWARD_SPEED = 300;
    private static final int ROTATE_SPEED = 150;
    public Direction startingDir;
    public Direction endingDir;

    public int deterministicPositioning() {

        //printGrid(plane);

        ArrayList<Motion> motionTrace = new ArrayList<Motion>();
        lm.setAcceleration(500);
        rm.setAcceleration(500);
        while(countPossibilities(this.plane) > 1) {
        	//printGrid(plane);
        	//RConsole.println("Iterating");
        	sleep();        	
            int distanceToWall = getFilteredData();
            //RConsole.println(Integer.toString(distanceToWall));
            if (distanceToWall < 24) {
            	//RConsole.println("ROTATING");
                simulateOnAllTiles(Obstacle.OBSTACLE, motionTrace, plane);
                rotate90CounterClock();
                motionTrace.add(Motion.ROTATE);
            } else {
            	//RConsole.println("FORWARD");
                simulateOnAllTiles(Obstacle.CLEAR, motionTrace, plane);
                moveForward();
                motionTrace.add(Motion.FORWARD);
            }
        }
        
        Coordinate startingPosition = findStartingPosition();
        //RConsole.println("y = " + Integer.toString(startingPosition.getY()) + " x = " + Integer.toString(startingPosition.getX()));
        Coordinate endingPosition = findEndingPosition(motionTrace, startingPosition);
        moveToPlaneCorner(endingPosition);

        LCD.drawString("algo solved", 0, 0);
        //printGrid(plane);
        return countPossibilities(plane);
    }
    
    public int stochasticPositioning() {

        //printGrid(plane);

        ArrayList<Motion> motionTrace = new ArrayList<Motion>();
        lm.setAcceleration(500);
        rm.setAcceleration(500);
        while(countPossibilities(this.plane) > 1) {
        	//printGrid(plane);
        	//RConsole.println("Iterating");
        	sleep();        	
            int distanceToWall = getFilteredData();
            //RConsole.println(Integer.toString(distanceToWall));
            if (distanceToWall < 24) {
            	//RConsole.println("ROTATING");
                simulateOnAllTiles(Obstacle.OBSTACLE, motionTrace, plane);
                rotate90CounterClock();
                motionTrace.add(Motion.ROTATE);
            } else {
            	//RConsole.println("FORWARD");
            	
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
        //RConsole.println("y = " + Integer.toString(startingPosition.getY()) + " x = " + Integer.toString(startingPosition.getX()));
        Coordinate endingPosition = findEndingPosition(motionTrace, startingPosition);
        moveToPlaneCorner(endingPosition);

        LCD.drawString("algo solved", 0, 0);
        //printGrid(plane);
        return countPossibilities(plane);
    }
    
    public boolean getRandomBoolean() {
        return Math.random() < 0.5;
        //I tried another approaches here, still the same result
    }
    
    
    public void moveToPlaneCorner(Coordinate endingPosition) {
		Direction currentDirection;
    	int y = endingPosition.getY();
		int x = endingPosition.getX();
		rotateNorth(null);
		currentDirection = Direction.NORTH;
		//RConsole.println("y = " + Integer.toString(y) + " x = " + Integer.toString(x));
		while (true) {
			sleep();
			
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
				//rotate west
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

	public Coordinate findEndingPosition(ArrayList<Motion> motionTrace, Coordinate startingPosition) {
		//RConsole.println("row = " + Integer.toString(startingPosition[1]) + " col = " + Integer.toString(startingPosition[0]));
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
		Coordinate coord = new Coordinate(vr.getX(), vr.getY());
		return coord;
    }
	
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
	
    public void rotate90CounterClock() {
    	lm.setSpeed(ROTATE_SPEED);
        rm.setSpeed(ROTATE_SPEED);
        lm.rotate(convertAngle(-90), true);
        rm.rotate(convertAngle(90), false);
    }
    
    public void rotate90ClockWise() {
    	lm.setSpeed(ROTATE_SPEED);
        rm.setSpeed(ROTATE_SPEED);
        lm.rotate(convertAngle(90), true);
        rm.rotate(convertAngle(-90), false);
    }
    
    public void moveForward() {
    	lm.setSpeed(FORWARD_SPEED);
        rm.setSpeed(FORWARD_SPEED);
        lm.rotate(convertDistance(30), true);
        rm.rotate(convertDistance(30), false);
    }


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
                            	//RConsole.println("SETTING i = " + i + " j = " + j + " to false");
                                plane[i][j].setPossibilityToFalse(selectedDir);
                            }
                        }
                    }
                }
            }
        }
    }

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

        int x = countPossibilities(plane);

        return plane;
    }

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

    public void printGrid(Tile[][] plane) {
        
        Direction[] directions = { Direction.NORTH, 
                            Direction.SOUTH,
                            Direction.EAST,
                            Direction.WEST 
                          };

        for(Direction dir : directions) {

            if (dir == Direction.NORTH) {
                RConsole.println("North possibilities");
            } else if (dir == Direction.SOUTH) {
                RConsole.println("South possibilities");
            } else if (dir == Direction.EAST) {
                RConsole.println("East possibilities");
            } else {
                RConsole.println("West possibilities");
            }

            for(int i = 0; i < 4; i++)
            {
                for(int j = 0; j < 4; j++)
                {
                    RConsole.print(String.valueOf(plane[i][j].isPossible(dir)) + "  ");
                }
                RConsole.println("");
            }
        }
    }

    public DeterministicLocalization(UltrasonicSensor us, NXTRegulatedMotor leftMotor, NXTRegulatedMotor rightMotor) {
        this.plane = createPlane();
        this.us = us;
        this.lm = leftMotor;
        this.rm = rightMotor;
    }

    private int convertDistance(double distance) {
        return (int) ((180.0 * distance) / (Math.PI * this.WHEEL_RADIUS));
    }

    private int convertAngle(double angle) {
        return convertDistance(Math.PI * this.WHEEL_DISTANCE * angle / 360.0);
    }
    
    private int getFilteredData() {
		int distance;
		int[] dist = new int[5];
		for (int i = 0; i < 5; i++) {
			us.ping();

			// wait for ping to complete
			try { Thread.sleep(50); } catch (InterruptedException e) {}

			// there will be a delay
			dist[i] = us.getDistance();

		}

		// sort the array to take the median
		// take values in the array sequentially
		findMedian(dist);
		// take the middle value in the array which is the median and return it
		distance = dist[2];
				
		return distance;
	}
	
	private void findMedian(int[] dist) {
		// sort the array to take the median
		// take values in the array sequentially
		for (int j = 0; j < 5; j++) {
			int min = dist[j];
			int pos = j;

			// find the min value in the remaining part of the array
			for (int k = j; k < 5; k++) {
				if (dist[k] < min) {
					min = dist[k];
					pos = k;
				}
			}

			// set the first position of the unsorted array to the min
			int temp = dist[j];
			dist[j] = min;
			dist[pos] = temp;

		}
	}
	
	public void sleep() {
		try { Thread.sleep(1000); } catch (InterruptedException e) {};
	}

    /*
    public static void main(String[] args) {

        Tile[][] plane;
        this.plane = createPlane();

        int x = deterministicPositioning(plane);

    }
    */

}
