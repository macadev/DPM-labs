//tiles on the grid, use for representing the plane.
public class Tile {

    public boolean isObstacle;
    public boolean possibleN;
    public boolean possibleS;
    public boolean possibleW;
    public boolean possibleE;
    public boolean obstacleN;
    public boolean obstacleS;
    public boolean obstacleW;
    public boolean obstacleE;

    public Tile() {
        this.isObstacle = false;
        this.possibleN = true;
        this.possibleS = true;
        this.possibleW = true;
        this.possibleE = true;
        this.obstacleN = false;
        this.obstacleS = false;
        this.obstacleW = false;
        this.obstacleE = false;
    }

    public boolean hasObstacle(Direction dir) {
        if (dir == Direction.NORTH) {
            return this.obstacleN;
        } else if (dir == Direction.WEST) {
            return this.obstacleW;
        } else if (dir == Direction.EAST) {
            return this.obstacleE;
        } else {
            return this.obstacleS;
        }
    }

    public void setDirectionToFalse(Direction dir) {
        if (dir == Direction.NORTH) {
            this.possibleN = false;
        } else if (dir == Direction.WEST) {
            this.possibleW = false;
        } else if (dir == Direction.EAST) {
            this.possibleE = false;
        } else {
            this.possibleS = false;
        }
    }

}
