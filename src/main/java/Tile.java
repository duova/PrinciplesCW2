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

    public char render() {
        if (pieces.isEmpty()) {
            return isWall ? '#' : '.';
        }
        return pieces.getLast().getDisplayLetter();
    }

    public boolean useCommandOnTile(char interactChar, Creature creature) {
        //returns whether command interacted and if so the player doesn't interact with deeper pieces.
        //deletes the piece as well.
        for (int i = pieces.size() - 1; i >= 0; i--) {
            if (pieces.get(i) instanceof Interactable interactable) {
                if (interactable.displayLetter == interactChar) {
                    interactable.onInteract();
                    pieces.remove(i);
                    return true;
                }
            }
        }
        return false;
    }
}
