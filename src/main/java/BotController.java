import java.util.*;

public class BotController implements Controller {
    Creature player;
    Stack<Direction> directions = new Stack<Direction>();
    BotCommand command;
    boolean canExit;


    @Override
    public void linkPlayer(Creature player) {
        this.player = player;
    }

    @Override
    public void executeTurn() {
        switch (command) {
            case NONE -> {
                selectCommand();
            }
            case WANDER, CATCH -> {
                if (!directions.empty()) {
                    player.moveCommand(directions.pop());
                }
                else {
                    command = BotCommand.NONE;
                    selectCommand();
                }
            }
            case PICKUP -> {
                if (!directions.empty()) {
                    player.moveCommand(directions.pop());
                }
                else {
                    player.pickUpCommand();
                    command = BotCommand.NONE;
                }
            }
            case EXIT -> {
                if (!directions.empty()) {
                    player.moveCommand(directions.pop());
                }
                else {
                    player.quitCommand();
                    command = BotCommand.NONE;
                }
            }
        }
    }

    private void selectCommand() {
        LookCommandResult lookResult = player.lookCommand();
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
            if (piece instanceof Creature && piece != player) {
                target = player;
                command = BotCommand.CATCH;
                break;
            }
        }
        if (target == null) {
            if (!canExit) {
                for (Piece piece : possibleTargets) {
                    if (piece instanceof Gold) {
                        target = piece;
                        command = BotCommand.PICKUP;
                        break;
                    }
                }
            }
            else {
                for (Piece piece : possibleTargets) {
                    if (piece instanceof Exit) {
                        target = piece;
                        command = BotCommand.EXIT;
                        break;
                    }
                }
            }
        }
        if (target == null) wander(lookResult.visibleMap);
        else {
            boolean canCalculatePath = calculatePath(lookResult.visibleMap,
                    player.getX() - lookResult.visibleMap.xTranslation,
                    player.getY() - lookResult.visibleMap.yTranslation,
                    target.getX() - lookResult.visibleMap.xTranslation,
                    target.getY() - lookResult.visibleMap.yTranslation);
            if (!canCalculatePath) wander(lookResult.visibleMap);
        }
    }

    private void wander(MapView view) {
        command = BotCommand.WANDER;
        int count = view.tiles.length * view.tiles[0].length;
        generateRandomPath(view, count);
    }

    private void generateRandomPath(MapView view, int count) {
        Random rand = new Random();
        int randomNumber = rand.nextInt(count);
        int i = 0;
        for (int x = 0; x < view.tiles.length; x++) {
            for (int y = 0; y < view.tiles[0].length; y++) {
                if (i == randomNumber) {
                    if (view.tiles[x][y].isWall || !calculatePath(view, 2, 2, x, y)) {
                        generateRandomPath(view, count);
                    }
                    break;
                }
                i++;
            }
        }
    }

    private boolean calculatePath(MapView view, int originLocalX, int originLocalY, int targetLocalX, int targetLocalY) {
        //Minimal A* pathfinding impl.
        Tile targetTile = view.tiles[targetLocalX][targetLocalY];
        for (int x = 0; x < view.tiles.length; x++) {
            for (int y = 0; y < view.tiles[0].length; y++) {
                Tile tile = view.tiles[x][y];
                //Cache necessary information.
                tile.x = x;
                tile.y = y;
                tile.adjacentTiles[0] = coordinatesExist(view.tiles, x, y + 1) ? view.tiles[x][y + 1] : null;
                tile.adjacentTiles[1] = coordinatesExist(view.tiles, x + 1, y) ? view.tiles[x + 1][y] : null;
                tile.adjacentTiles[2] = coordinatesExist(view.tiles, x, y - 1) ? view.tiles[x][y - 1] : null;
                tile.adjacentTiles[3] = coordinatesExist(view.tiles, x - 1, y) ? view.tiles[x - 1][y] : null;
            }
        }
        ArrayList<Tile> openList = new ArrayList<Tile>();
        ArrayList<Tile> closedList = new ArrayList<Tile>();
        openList.add(view.tiles[originLocalX][originLocalY]);
        while (true) {
            openList.sort(Comparator.comparing(Tile::GetFValue));
            Tile currentTile = openList.getFirst();
            closedList.add(currentTile);
            if (currentTile == targetTile) break;
            openList.remove(currentTile);
            for (Tile tile : currentTile.adjacentTiles) {
                if (tile.isWall) continue;
                if (closedList.contains(tile)) continue;
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
            int directionIndex = 0;
            for (int i = 0; i < 4; i++) {
                if (reverseCurrentTile.parent.adjacentTiles[i] == reverseCurrentTile) {
                    directionIndex = i;
                }
            }
            switch (directionIndex) {
                case 0:
                    directions.push(Direction.N);
                    break;
                case 1:
                    directions.push(Direction.E);
                    break;
                case 2:
                    directions.push(Direction.S);
                    break;
                case 3:
                    directions.push(Direction.W);
                    break;
            }
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
