public abstract class Piece {
    private int x;

    public int getX() {
        return x;
    }

    private int y;

    public int getY() {
        return y;
    }

    private DungeonMap map;

    public DungeonMap getMap() {
        return map;
    }

    public Piece(int x, int y, DungeonMap map) {
        this.x = x;
        this.y = y;
        this.map = map;
    }

    /**
     * Sets new location of the piece without performing checks but updates the map.
     * @param x New location x coordinates.
     * @param y New location y coordinates.
     */
    public void setLocation(int x, int y) {
        map.tiles[this.x][this.y].pieces.remove(this);
        this.x = x;
        this.y = y;
        map.tiles[x][y].pieces.add(this);
    }

    /***
     * Gets char used to represent this piece visually.
     * @return
     */
    public abstract char getDisplayLetter();
}
