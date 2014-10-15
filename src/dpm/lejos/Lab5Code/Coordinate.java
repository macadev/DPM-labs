/**
 * Coordinate class abstracts the x,y coordinate pair
 */
public class Coordinate {
	private int x;
	private int y;

    /**
     * default constructor takes initial positions
     * @param x the initial x position
     * @param y the initial y position
     */
	public Coordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}

    /**
     * public accessor for the x component
     * @return the x component
     */
	public int getX() {
		return this.x;
	}

    /**
     * public accessor for the y component
     * @return the y component
     */
	public int getY() {
		return this.y;
	}

    /**
     * public setter for the y component
     * @param y the new y component
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * public setter for the x component
     * @param x the new x component
     */
    public void setX(int x) {
        this.x = x;
    }
}
