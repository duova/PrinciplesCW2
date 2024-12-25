public class Creature extends Piece {

    //We ensure that they have a turn when they use turn-based functions to prevent badly implemented controllers
    //from violating the one action per turn rule.
    private boolean hasTurn = false;

    private int goldToWin;

    private int currentGold = 0;

    private int sightWidth;

    private int sightHeight;

    private char letter;

    public Creature(CreatureArgs args) {
        super(args.x, args.y, args.map);
        goldToWin = args.map.winCount;
        sightWidth = args.sightWidth;
        sightHeight = args.sightHeight;
        letter = args.letter;
    }

    public void allowCreatureToAct() {
        hasTurn = true;
    }

    public LookCommandResult lookCommand() {
        if (!hasTurn) new LookCommandResult(false, null);
        hasTurn = false;
        MapView view = new MapView(getMap(),
                getX() - 2, getX() + 2,
                getY() - 2, getY() + 2);
        return new LookCommandResult(true, view);
    }

    public HelloCommandResult helloCommand() {
        if (!hasTurn) new HelloCommandResult(false, 0);
        hasTurn = false;
        return new HelloCommandResult(true, goldToWin);
    }

    public GoldCommandResult goldCommand() {
        if (!hasTurn) return new GoldCommandResult(false, 0);
        hasTurn = false;
        return new GoldCommandResult(true, currentGold);
    }

    public PickUpCommandResult pickUpCommand() {
        if (!hasTurn) return new PickUpCommandResult(false, currentGold);
        hasTurn = false;
        if (interact('G')) {
            currentGold++;
            return new PickUpCommandResult(true, currentGold);
        }
        return new PickUpCommandResult(false, currentGold);
    }

    public CommandResult moveCommand(Direction direction) {
        
    }

    public QuitCommandResult quitCommand() {

    }

    private boolean interact(char interactChar) {
        Tile tile = getMap().tiles[getX()][getY()];
        for (int i = tile.pieces.size() - 1; i >= 0; i--) {
            if (tile.pieces.get(i) instanceof Interactable interactable) {
                if (interactable.useCommandOnTile(interactChar, this)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public char getDisplayLetter() {
        return letter;
    }
}