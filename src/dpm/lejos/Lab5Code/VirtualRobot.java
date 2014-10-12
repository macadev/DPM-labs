package dpm.lejos.Lab5Code;

/**
 * Class to track the position and path of the robot. This allows us to compare the movements of the robot to compare with the obstacle position
 * @author danielmacario
 */
public class VirtualRobot {

    private int x;
    private int y;
    private Direction dir;
    private final static Object lock = new Object();

    public VirtualRobot (int x, int y, Direction dir) {
        this.x = x;
        this.y = y;
        this.dir = dir;
    }

    /**
     * Increment the position of the robot by one in the current direction
     */
    public void moveForward() {
        synchronized (lock) {
            if (this.dir == Direction.NORTH) {
                y++;
            } else if (this.dir == Direction.SOUTH) {
                y--;
            } else if (this.dir == Direction.EAST) {
                x++;
            } else {
                x--;
            }
        }
    }

    /**
     * Rotate the robot counter-clockwise
     */
    public void rotate() {
        synchronized (lock) {
            if (this.dir == Direction.NORTH) {
                this.dir = Direction.WEST;
            } else if (this.dir == Direction.WEST) {
                this.dir = Direction.SOUTH;
            } else if (this.dir == Direction.SOUTH) {
                this.dir = Direction.EAST;
            } else if (this.dir == Direction.EAST) {
                this.dir = Direction.NORTH;
            }
        }
    }

    public boolean hasWallAhead(Tile[][] plane) {
        synchronized (lock) {
            if (this.dir == Direction.NORTH) {
                return plane[y][x].obstacleN;
            } else if (this.dir == Direction.SOUTH) {
                return plane[y][x].obstacleS;
            } else if (this.dir == Direction.EAST) {
                return plane[y][x].obstacleE;
            } else {
                return plane[y][x].obstacleW;
            }
        }
    }

    /**
     * public setter for x position
     * @param x new x position
     */
    public void setX(int x) {
        synchronized (lock) {
            this.x = x;
        }
    }

    /**
     * public direction setter
     * @param dir new direction
     */
    public void setDir(Direction dir) {
        synchronized (lock) {
            this.dir = dir;
        }
    }

    /**
     * public y position setter
     * @param y new y position
     */
    public void setY(int y) {
        synchronized (lock) {
            this.y = y;
        }
    }

    /**
     * public direction accessor
     * @return current direction
     */
    public Direction getDir() {
        synchronized (lock) {
            return dir;
        }
    }

    /**
     * public x position accessor
     * @return current x position
     */
    public int getX() {
        synchronized (lock) {
            return x;
        }
    }

    /**
     * public y position accessor
     * @return current y position
     */
    public int getY() {
        synchronized (lock) {
            return y;
        }
    }
}
