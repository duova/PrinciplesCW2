import java.util.*;

/**
 * The implementation of the controller for the bot.
 */
public class BotController implements Controller {
    private Creature creature;
    //Queued directions for the bot. A stack is used as the A* algorithm produces a path backwards.
    private Stack<Direction> directions = new Stack<Direction>();
    //Current command that is being executed by the bot. This is persistent across turns as multiple turns are required for most.
    private BotCommand command = BotCommand.NONE;
    //A gold estimate is saved as it would be inefficient to check the gold count constantly.
    private int goldCollected;
    //Retrieved at the start.
    private int requiredGold = -1;

    private final Random rand = new Random();

    @Override
    public void linkPlayer(Creature player) {
        this.creature = player;
    }

    @Override
    public void executeTurn() {
        if (requiredGold == -1) {
            requiredGold = creature.helloCommand().goldToWin;
            return;
        }
        //We make sure each turn performs no more than one command, though there is a check in creature that prevents
        //more than one command from being run each turn anyway. Each turn is used optimally and as much information
        //is cached as possible, since the controller respects the rules that the player has.
        switch (command) {
            case NONE -> {
                selectCommand();
            }
            case WANDER, CATCH -> {
                creature.moveCommand(directions.pop());
                if (directions.empty()) {
                    command = BotCommand.NONE;
                }
            }
            case PICKUP -> {
                if (!directions.empty()) {
                    creature.moveCommand(directions.pop());
                }
                else {
                    goldCollected = creature.pickUpCommand().currentGold;
                    command = BotCommand.NONE;
                }
            }
            case EXIT -> {
                if (!directions.empty()) {
                    creature.moveCommand(directions.pop());
                }
                else {
                    //Shouldn't need to recheck as in order for this command to be used it must have enough gold.
                    creature.quitCommand();
                    System.out.println("LOSE (Bot Exited First)");
                    Main.freeze = true;
                    command = BotCommand.NONE;
                }
            }
        }
    }

    /**
     * Decides on what to do for the coming turns.
     */
    private void selectCommand() {
        directions.clear();
        LookCommandResult lookResult = creature.lookCommand();
        //Note that we get a MapView here which is only the section of the map that the bot can see.
        Tile[][] tiles = lookResult.visibleMap.tiles;
        ArrayList<Piece> possibleTargets = new ArrayList<Piece>();
        for (int x = 0; x < tiles.length; x++) {
            for (int y = 0; y < tiles[0].length; y++) {
                Tile tile = tiles[x][y];
                possibleTargets.addAll(tile.pieces);
            }
        }
        Piece target = null;
        //Prioritize player.
        for (Piece piece : possibleTargets) {
            if (piece instanceof Creature && piece != creature) {
                target = piece;
                command = BotCommand.CATCH;
                break;
            }
        }
        //Otherwise get enough gold and exit assuming they are within view.
        if (target == null) {
            if (goldCollected >= requiredGold) {
                for (Piece piece : possibleTargets) {
                    if (piece instanceof Exit) {
                        target = piece;
                        command = BotCommand.EXIT;
                        break;
                    }
                }
            } else {
                for (Piece piece : possibleTargets) {
                    if (piece instanceof Gold) {
                        target = piece;
                        command = BotCommand.PICKUP;
                        break;
                    }
                }
            }
        }
        //If nothing can be seen then we just pick a random location within view and walk over.
        if (target == null) wander(lookResult.visibleMap);
        else {
            //If a target has been select we calculate a path for it.
            boolean canCalculatePath = calculatePath(lookResult.visibleMap,
                    creature.getX() - lookResult.visibleMap.xTranslation,
                    creature.getY() - lookResult.visibleMap.yTranslation,
                    target.getX() - lookResult.visibleMap.xTranslation,
                    target.getY() - lookResult.visibleMap.yTranslation);
            if (!canCalculatePath) wander(lookResult.visibleMap);
        }
    }

    /***
     * Sets bot to wander.
     * @param view Bot's view.
     */
    private void wander(MapView view) {
        command = BotCommand.WANDER;
        generateRandomPath(view);
    }

    /***
     * Generates a path to a random location within a given view.
     * @param view Provided view.
     */
    private void generateRandomPath(MapView view) {
        int randomNumber = rand.nextInt(view.tiles.length * view.tiles[0].length);
        int i = 0;
        for (int x = 0; x < view.tiles.length; x++) {
            for (int y = 0; y < view.tiles[0].length; y++) {
                if (i == randomNumber) {
                    //We have to re-randomize if the random number is a wall, doesn't have a viable path, or is the location we're already at.
                    if (view.tiles[x][y].isWall || !calculatePath(view, 2, 2, x, y) || x == 2 && y == 2) {
                        generateRandomPath(view);
                    }
                    return;
                }
                i++;
            }
        }
    }

    /***
     * An implementation of A* that uses tiles as nodes. Path is written to the destination stack.
     * @param view View of the map used.
     * @param originLocalX Origin x relative to the view.
     * @param originLocalY Origin y relative to the view.
     * @param targetLocalX Target x relative to the view.
     * @param targetLocalY Target y relative to the view.
     * @return Whether a path was found.
     */
    private boolean calculatePath(MapView view, int originLocalX, int originLocalY, int targetLocalX, int targetLocalY) {
        Tile targetTile = view.tiles[targetLocalX][targetLocalY];
        for (int x = 0; x < view.tiles.length; x++) {
            for (int y = 0; y < view.tiles[0].length; y++) {
                Tile tile = view.tiles[x][y];
                tile.resetPathfinding();
                //Cache necessary information.
                tile.x = x;
                tile.y = y;
                tile.adjacentTiles[0] = coordinatesExist(view.tiles, x, y + 1) ? view.tiles[x][y + 1] : null;
                tile.adjacentTiles[1] = coordinatesExist(view.tiles, x + 1, y) ? view.tiles[x + 1][y] : null;
                tile.adjacentTiles[2] = coordinatesExist(view.tiles, x, y - 1) ? view.tiles[x][y - 1] : null;
                tile.adjacentTiles[3] = coordinatesExist(view.tiles, x - 1, y) ? view.tiles[x - 1][y] : null;
            }
        }
        //Minimal A* impl.
        ArrayList<Tile> openList = new ArrayList<>();
        ArrayList<Tile> closedList = new ArrayList<>();
        openList.add(view.tiles[originLocalX][originLocalY]);
        while (true) {
            if (openList.isEmpty()) return false;
            openList.sort(Comparator.comparing(Tile::GetFValue));
            Tile currentTile = openList.getFirst();
            closedList.add(currentTile);
            if (currentTile == targetTile) break;
            openList.remove(currentTile);
            for (Tile tile : currentTile.adjacentTiles) {
                //Walls should be avoided.
                if (tile == null || tile.isWall || closedList.contains(tile)) continue;
                if (!openList.contains(tile)) {
                    openList.add(tile);
                    tile.parent = currentTile;
                    tile.gValue = currentTile.gValue++;
                    tile.hValue = getDistanceSquared(tile.x, tile.y, targetLocalX, targetLocalY);
                    tile.fValue = tile.hValue + tile.gValue;
                }
                else if (tile.gValue > currentTile.gValue) {
                    tile.parent = currentTile;
                    tile.gValue = currentTile.gValue++;
                    tile.fValue = tile.gValue + tile.hValue;
                }
            }
        }
        Tile reverseCurrentTile = targetTile;
        while (reverseCurrentTile.parent != null) {
            Direction direction = getDirection(reverseCurrentTile);
            //Push directions onto a stack as they're retrieved in reverse.
            directions.push(direction);
            reverseCurrentTile = reverseCurrentTile.parent;
        }
        if (reverseCurrentTile.x == originLocalX && reverseCurrentTile.y == originLocalY) {
            return true;
        }
        else {
            directions.clear();
            return false;
        }
    }

    private static Direction getDirection(Tile reverseCurrentTile) {
        int directionIndex = 0;
        for (int i = 0; i < 4; i++) {
            if (reverseCurrentTile.parent.adjacentTiles[i] == reverseCurrentTile) {
                directionIndex = i;
            }
        }
        return switch (directionIndex) {
            case 0 -> Direction.N;
            case 1 -> Direction.E;
            case 2 -> Direction.S;
            case 3 -> Direction.W;
            default -> throw new IllegalStateException("Unexpected value: " + directionIndex);
        };
    }

    private float getDistanceSquared(float x1, float y1, float x2, float y2) {
        float x = x2 - x1;
        float y = y2 - y1;
        return x * x + y * y;
    }

    private boolean coordinatesExist(Tile[][] tileMap, int x, int y) {
        if (x >= tileMap.length || x < 0) return false;
        if (y >= tileMap[0].length || y < 0) return false;
        return true;
    }
}
