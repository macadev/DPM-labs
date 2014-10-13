package dpm.lejos.Lab5Code;

import java.util.ArrayList;

/**
 *
 * @author Daniel Macario
 * @version 1.0
 */
public class DeterministicLocalization {


    public static int deterministicPositioning(Tile[][] plane) {
        int x = 2;
        int y = 0;
        Direction dir = Direction.NORTH;
        VirtualRobot vrt = new VirtualRobot(x, y, dir);

        ArrayList<Motion> motionTrace = new ArrayList<Motion>();

        while(countPossibilities(plane) > 1) {

            if (vrt.hasWallAhead(plane)) {
                simulateOnAllTiles(Obstacle.OBSTACLE, motionTrace, plane);
                vrt.rotate();
                motionTrace.add(Motion.ROTATE);
            } else {
                simulateOnAllTiles(Obstacle.CLEAR, motionTrace, plane);
                vrt.moveForward();
                motionTrace.add(Motion.FORWARD);
            }
        }

        printGrid(plane);
        return countPossibilities(plane);
    }


    public static void simulateOnAllTiles(Obstacle obs, ArrayList<Motion> motionTrace, Tile[][] plane) {
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

    public static int countPossibilities(Tile[][] plane) {
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

    public static Tile[][] createPlane() {
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

    public static void fillRemainingPositions(Tile[][] plane) {
        plane[0][1].setObstacle(Direction.WEST, true);
        plane[0][2].setObstacle(Direction.SOUTH, true);
        plane[0][3].setObstacle(Direction.SOUTH, true);
        plane[1][0].setObstacle(Direction.NORTH, true);
        plane[1][1].setObstacle(Direction.EAST, true);
        plane[2][2].setObstacle(Direction.NORTH, true);
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

    public static void printGrid(Tile[][] plane) {
        
        Direction[] directions = { Direction.NORTH, 
                            Direction.SOUTH,
                            Direction.EAST,
                            Direction.WEST 
                          };

        for(Direction dir : directions) {

            if (dir == Direction.NORTH) {
                System.out.println("North possibilities");
            } else if (dir == Direction.SOUTH) {
                System.out.println("South possibilities");
            } else if (dir == Direction.EAST) {
                System.out.println("East possibilities");
            } else {
                System.out.println("West possibilities");
            }

            for(int i = 0; i < 4; i++)
            {
                for(int j = 0; j < 4; j++)
                {
                    System.out.printf(String.valueOf(plane[i][j].isPossible(dir)) + "  ");
                }
                System.out.println();
            }
        }
    }


    public static void main(String[] args) {

        //First we will need to enconde the grid;

        Tile[][] plane = new Tile[4][4];
        plane = createPlane();

        int x = deterministicPositioning(plane);
        System.out.println(x);



    }

}
