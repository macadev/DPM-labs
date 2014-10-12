/**
 * Created by danielmacario on 2014-10-11.
 */
public class VirtualRobot {

    public int x;
    public int y;
    public Direction dir;

    public VirtualRobot (int x, int y, Direction dir) {
        this.x = x;
        this.y = y;
        this.dir = dir;
    }

    public void moveForward() {
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

    public void rotate() {
        if (this.dir == Direction.NORTH) {
            this.dir = Direction.WEST;
        } else if (this.dir == Direction.SOUTH) {
            this.dir = Direction.EAST;
        } else if (this.dir == Direction.EAST) {
            this.dir = Direction.NORTH;
        } else {
            this.dir = Direction.SOUTH;
        }
    }

    public boolean hasWallAhead(Tile[][] plane) {
        if (this.dir == Direction.NORTH) {
            if (plane[y][x].obstacleN) return true;
            return false;
        } else if (this.dir == Direction.SOUTH) {
            if (plane[y][x].obstacleS) return true;
            return false;
        } else if (this.dir == Direction.EAST) {
            if (plane[y][x].obstacleE) return true;
            return false;
        } else {
            if (plane[y][x].obstacleW) return true;
            return false;
        }
    }

}
