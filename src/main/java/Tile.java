import java.util.ArrayList;

public class Tile {
    public ArrayList<Piece> pieces = new ArrayList<Piece>(); //Last piece is rendered.

    public boolean isWall; //We make tiles optionally walls as making walls a piece seems excessive with type checking.

    //For pathfinding.
    public Tile[] adjacentTiles = new Tile[4];
    public float fValue;
    public float gValue;
    public float hValue;
    public int x;
    public int y;
    public Tile parent;
    public float GetFValue() {
        return fValue;
    }

    /***
     * Resets the variables used for pathfinding to make sure they don't affect the next operation.
     */
    public void resetPathfinding() {
        adjacentTiles = new Tile[4];
        fValue = 0;
        gValue = 0;
        hValue = 0;
        x = 0;
        y = 0;
        parent = null;
    }

    /***
     * Gets the char that should be used to display this tile.
     * @return
     */
    public char render() {
        if (pieces.isEmpty()) {
            return isWall ? '#' : '.';
        }
        return pieces.getLast().getDisplayLetter();
    }

    /***
     * Finds the top-most interactable on this tile and attempts to interact with it if it has the given interactChar.
     * Will iterate through the available pieces if the top one does not have the correct char.
     * @param interactChar The char to check the interactable against.
     * @param creature The creature that interacted.
     * @return
     */
    public boolean useCommandOnTile(char interactChar, Creature creature) {
        //returns whether command interacted and if so the player doesn't interact with deeper pieces.
        //deletes the piece as well.
        for (int i = pieces.size() - 1; i >= 0; i--) {
            if (pieces.get(i) == creature) continue;
            if (pieces.get(i) instanceof Interactable interactable) {
                if (interactable.getDisplayLetter() == interactChar) {
                    interactable.onInteract(creature);
                    pieces.remove(i);
                    return true;
                }
            }
        }
        return false;
    }
}
