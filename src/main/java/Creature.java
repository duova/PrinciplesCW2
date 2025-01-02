/***
 * Creatures are the representation of moving and acting players, both human and player controlled.
 * They're named creature to prevent confusion with the actual human player, as they can be played by the computer.
 */
public class Creature extends Piece {
    //We ensure that they have a turn when they use turn-based functions to prevent badly implemented controllers
    //from violating the one action per turn rule.
    private boolean hasTurn = false;

    private int goldToWin;

    private int currentGold = 0;

    //The width of the area that the creature can see.
    private int sightWidth;

    //The height of the area that the creature can see.
    private int sightHeight;

    //Letter used to represent this creature.
    private char letter;

    //Controller used to control this creature.
    private Controller controller;

    public Creature(CreatureArgs args) {
        super(args.x, args.y, args.map);
        goldToWin = args.map.winCount;
        sightWidth = args.sightWidth;
        sightHeight = args.sightHeight;
        letter = args.letter;
        controller = args.controller;
        //Link the controller to the player so it knows what to call functions on.
        controller.linkPlayer(this);
        getMap().tiles[args.x][args.y].pieces.add(this);
    }

    public void addGold() {
        currentGold++;
    }

    /***
     * A creature has to be allowed to act before it may act to ensure creatures don't take more than one action per
     * turn. Controllers are designed not to do so but this check is for encapsulation.
     */
    public void allowCreatureToAct() {
        hasTurn = true;
    }

    /***
     * Called every turn to perform an action.
     */
    public void act() {
        controller.executeTurn();
    }

    /***
     * The creatures implementation of looking shared by players and bots.
     * @return The result of looking.
     */
    public LookCommandResult lookCommand() {
        if (!hasTurn) new LookCommandResult(false, null);
        hasTurn = false;
        MapView view = new MapView(getMap(),
                getX() - ((sightWidth - 1) / 2), getX() + ((sightWidth - 1) / 2),
                getY() - ((sightHeight - 1) / 2), getY() + ((sightHeight - 1) / 2));
        return new LookCommandResult(true, view);
    }

    /***
     * The creatures implementation of the hello command shared by players and bots.
     * @return The result of the hello command.
     */
    public HelloCommandResult helloCommand() {
        if (!hasTurn) new HelloCommandResult(false, 0);
        hasTurn = false;
        return new HelloCommandResult(true, goldToWin);
    }

    /***
     * The creatures implementation of looking shared by players and bots.
     * @return The result of looking.
     */
    public GoldCommandResult goldCommand() {
        if (!hasTurn) return new GoldCommandResult(false, 0);
        hasTurn = false;
        return new GoldCommandResult(true, currentGold);
    }

    /***
     * The creatures implementation of pickup shared by players and bots.
     * @return The result of pickup.
     */
    public PickUpCommandResult pickUpCommand() {
        if (!hasTurn) return new PickUpCommandResult(false, currentGold);
        hasTurn = false;
        if (interact('G')) {
            return new PickUpCommandResult(true, currentGold);
        }
        return new PickUpCommandResult(false, currentGold);
    }

    /***
     * The creatures implementation of moving shared by players and bots.
     * @return The result of moving.
     */
    public CommandResult moveCommand(Direction direction) {
        if (!hasTurn) return new CommandResult(false);
        hasTurn = false;
        int xDelta = 0;
        int yDelta = 0;
        switch (direction) {
            case N:
                yDelta = 1;
                break;
            case E:
                xDelta = 1;
                break;
            case S:
                yDelta = -1;
                break;
            case W:
                xDelta = -1;
                break;
        }
        if (!getMap().tiles[getX() + xDelta][getY() + yDelta].isWall) {
            setLocation(getX() + xDelta,getY() + yDelta);
            return new CommandResult(true);
        }
        return new CommandResult(false);
    }

    /***
     * The creatures implementation of quitting shared by players and bots.
     * @return The result of quitting.
     */
    public QuitCommandResult quitCommand() {
        if (!hasTurn) return new QuitCommandResult(false, false);
        hasTurn = false;
        if (interact('E') && currentGold >= goldToWin) {
            return new QuitCommandResult(true, true);
        }
        return new QuitCommandResult(true, false);
    }

    /***
     * Attempts to interact with the current tile, calling onInteract() on the Interactables on the tile.
     * @param interactChar The char the Interactable must match to be used.
     * @return Whether the interaction was successful.
     */
    private boolean interact(char interactChar) {
        Tile tile = getMap().tiles[getX()][getY()];
        return tile.useCommandOnTile(interactChar, this);
    }

    @Override
    public char getDisplayLetter() {
        return letter;
    }
}