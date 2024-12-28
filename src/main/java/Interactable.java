public abstract class Interactable extends Piece {
    public String interactCommand;
    public char displayLetter;

    public Interactable(int x, int y, DungeonMap map) {
        super(x, y, map);
    }

    protected abstract void onInteract();
}