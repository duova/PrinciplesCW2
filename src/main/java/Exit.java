public class Exit extends Interactable {

    public Exit(int x, int y, DungeonMap map) {
        super(x, y, map);
    }

    @Override
    public char getDisplayLetter() {
        return 'E';
    }

    @Override
    public void onInteract() {

    }
}
