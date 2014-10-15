

//tiles on the grid, use for representing the plane.

/**
 * Class representing every unit tile on the playground board
 *
 * Each tile has 4 sides and measures 30 cm x 30 cm or 1 in x 1 in
 *
 * @author Daniel Macario
 */
public class Tile {

    private boolean isObstacle;
    private boolean possibleN;
    private boolean possibleS;
    private boolean possibleW;
    private boolean possibleE;
    private boolean obstacleN;
    private boolean obstacleS;
    private boolean obstacleW;
    private boolean obstacleE;

    /**
     * Default constructor
     *
     * Makes new tile where all sides are clear and tile is not an obstacle
     */
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

    /**
     * Get if there is an obstacle in certain direction
     * @param dir the direction to poll
     * @return presence of an obstacle in the polled direction
     */
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

    /**
     * Set the presence of an obstacle in a direction to false, i.e. clear
     * @param dir the direction to change
     */
    
    public void setPossibilityToFalse(Direction dir) {
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

    /**
     * General setter for the presence of an obstacle
     * @param dir the direction to change
     * @param value the desired value to set
     */
    public void setObstacle(Direction dir, boolean value) {
        if (dir == Direction.NORTH) {
            this.obstacleN = value;
        } else if (dir == Direction.WEST) {
            this.obstacleW = value;
        } else if (dir == Direction.EAST) {
            this.obstacleE = value;
        } else {
            this.obstacleS = value;
        }
    }

    /**
     * accessor for the presence of an obstacle in a given direction
     * @param dir the direction to poll
     * @return the existence of an obstacle in that direction
     */
    public boolean isPossible(Direction dir) {
        if (dir == Direction.NORTH) {
            return this.possibleN;
        } else if (dir == Direction.WEST) {
            return this.possibleW;
        } else if (dir == Direction.EAST) {
            return this.possibleE;
        } else {
            return this.possibleS;
        }
    }

    /**
     * Closes all possible starting points for the tile
     */    
    public void closeAllPossibilities() {
        this.possibleN = false;
        this.possibleS = false;
        this.possibleW = false;
        this.possibleE = false;
    }

    /**
     * Set if this tile is an obstacle
     * @param isObstacle the tile is an obstacle?
     */
    public void setObstacle(boolean isObstacle) {
        this.isObstacle = isObstacle;
    }

    /**
     * accessor for the state of the current tile
     * @return is the tile an obstacle
     */
    public boolean isObstacle() {
        return isObstacle;
    }
}
