public class Gold extends Interactable {

    public Gold(int x, int y, DungeonMap map) {
        super(x, y, map);
    }

    @Override
    public char getDisplayLetter() {
        return 'G';
    }

    @Override
    public void onInteract(Creature creature) {
        creature.addGold();
    }
}
