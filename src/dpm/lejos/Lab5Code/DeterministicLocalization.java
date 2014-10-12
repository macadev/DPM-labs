import java.util.ArrayList;
import java.util.Queue;

/**
 * Created by danielmacario on 2014-10-11.
 */
public class DeterministicLocalization {


    public static int deterministicPositioning(Tile[][] plane) {
        int x = 2;
        int y = 1;
        Direction dir = Direction.SOUTH;
        VirtualRobot vrt = new VirtualRobot(y,x,Direction.SOUTH);

        ArrayList<Motion> motionTrace = new ArrayList<Motion>();
//        System.out.println(1);
//        System.out.println(2);
//        System.out.println(3);
//        System.out.println(4);
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
            System.out.println(countPossibilities(plane) + " ");
        }
        System.out.println(countPossibilities(plane) + " ");
        return countPossibilities(plane);
    }


    public static void simulateOnAllTiles(Obstacle obs, ArrayList<Motion> motionTrace, Tile[][] plane) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                for (int k = 0; k < 4; k++) {
                    VirtualRobot vr;
                    if (k == 0) {
                        vr = new VirtualRobot(j, i, Direction.NORTH);
                    } else if (k == 1) {
                        vr = new VirtualRobot(j, i, Direction.EAST);
                    } else if (k == 2) {
                        vr = new VirtualRobot(j, i, Direction.WEST);
                    } else {
                        vr = new VirtualRobot(j, i, Direction.SOUTH);
                    }

                    if (!motionTrace.isEmpty()) {
                        for (Motion motion : motionTrace ) {
                            if (motion == Motion.FORWARD ) {
                                vr.moveForward();
                            } else {
                                vr.rotate();
                            }
                        }
                    }

                    if (plane[vr.x][vr.y].hasObstacle(vr.dir) && obs == Obstacle.OBSTACLE) {
                        //stays true
                    } else {
                        plane[vr.x][vr.y].setDirectionToFalse(vr.dir);
                    }

                }
            }
        }
    }

    public static int countPossibilities(Tile[][] plane) {
        int posCount = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (!plane[i][j].isObstacle) {
                    if (plane[i][j].possibleN) posCount++;
                    if (plane[i][j].possibleS) posCount++;
                    if (plane[i][j].possibleW) posCount++;
                    if (plane[i][j].possibleE) posCount++;
                }
            }
        }
        return posCount;
    }



    public static void main(String[] args) {

        //First we will need to enconde the grid;

        Tile[][] plane = new Tile[4][4];
        plane = createPlane();

        int x = deterministicPositioning(plane);
        //System.out.println("HI MAC");
        //System.out.println(x);



    }

    public static Tile[][] createPlane() {
        Tile[][] plane = new Tile[4][4];

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                plane[i][j] = new Tile();
            }
        }

        for (int i = 0; i < 4; i++) {
            plane[0][i].obstacleN = true;
            plane[3][i].obstacleS = true;
            plane[i][0].obstacleW = true;
            plane[i][3].obstacleE = true;
        }
        fillRemainingPositions(plane);

        int x = countPossibilities(plane);

        return plane;
    }

    public static void fillRemainingPositions(Tile[][] plane) {
        plane[0][1].obstacleW = true;
        plane[0][2].obstacleS = true;
        plane[0][3].obstacleS = true;
        plane[1][0].obstacleN = true;
        plane[1][1].obstacleE = true;
        plane[2][2].obstacleN = true;
        plane[2][3].obstacleN = true;
        plane[3][0].obstacleE = true;
        plane[3][2].obstacleW = true;
        plane[0][0].isObstacle = true;
        plane[1][2].isObstacle = true;
        plane[1][3].isObstacle = true;
        plane[3][1].isObstacle = true;
    }

    public static void printGrid(Tile[][] plane)
    {
        for(int i = 0; i < 4; i++)
        {
            for(int j = 0; j < 4; j++)
            {
                System.out.printf(String.valueOf(plane[i][j].obstacleN) + "  ");
            }
            System.out.println();
        }
    }

}
