/***
 * Interactables can be interacted with by creatures, calling their onInteract function through the Tile.
 */
public abstract class Interactable extends Piece {
    public Interactable(int x, int y, DungeonMap map) {
        super(x, y, map);
    }

    /***
     * Called when a creature interacts with this interactable.
     * @param creature Creature interacting.
     */
    protected abstract void onInteract(Creature creature);
}