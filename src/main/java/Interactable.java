public abstract class Interactable extends Piece {
    public String interactCommand;
    public char displayLetter;

    public Interactable(int x, int y, DungeonMap map) {
        super(x, y, map);
    }

    public boolean useCommandOnTile(char interactChar, Creature creature) {
        //returns whether command interacted and if so the player doesn't interact with deeper pieces.
        //deletes the piece as well.
        onInteract();
        return false;
    }

    protected abstract void onInteract();
}